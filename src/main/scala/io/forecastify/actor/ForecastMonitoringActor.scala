package io.forecastify.actor

import java.time.Instant
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, Props}
import akka.pattern.CircuitBreaker
import com.typesafe.scalalogging.LazyLogging
import io.forecastify.actor.ForecastMonitoringActor._
import io.forecastify.api.{OpenWeatherMapApiClient, WeatherForecastApi}
import io.forecastify.http.ForecastCache
import akka.pattern._
import com.typesafe.config.ConfigFactory
import io.forecastify.api.WeatherForecastApi.{Forecast, ForecastValue}
import io.forecastify.domain.Location.{CityName, Location, Temperature}
import io.forecastify.domain.TemperatureValidation
import io.forecastify.domain.TemperatureValidation.Valid

import scala.collection.mutable
import scala.concurrent.duration._
import scala.util.control.NonFatal

class ForecastMonitoringActor(cache: ForecastCache) extends Actor with LazyLogging {
  import context.dispatcher

  private lazy val breaker = new CircuitBreaker(
    context.system.scheduler,
    maxFailures = 10,
    callTimeout = 10.seconds,
    resetTimeout = 1.minute)

  private val config = context.system.settings.config
  private val refreshInterval = {
    val duration = config.getDuration("forecastify.refresh-interval", TimeUnit.MINUTES)
    FiniteDuration(duration, TimeUnit.MINUTES)
  }

  private lazy val client = new OpenWeatherMapApiClient()(context.system)

  private val state = new mutable.HashMap[CityName, List[StateValue]]()

  override def receive: Receive = {
    case c: Command => c match {
      case x: Initialize       =>
        val thresholds = x.locations.map(x => x.cityName -> x.threshold).toMap
        context.system.scheduler.schedule(0.seconds, refreshInterval) {
          breaker.withCircuitBreaker {
            logger.info(s"Performing request for locations: ${x.locations}")
            client.fetchForLocations(x.locations.map(_.cityName)).map(l => ReceivedForecast(l, thresholds))
          }.pipeTo(self).onFailure { case NonFatal(ex) =>
            logger.error("Failed to fetch weather forecast with exception: ", ex)
          }
        }
      case x: ReceivedForecast =>
        x.forecast foreach {
          case Right(res) =>
            logger.info(s"Updating state forecast for city: ${res.cityName}")
            updateCurrentState(res, x.thresholds)
          case Left(err)  =>
            logger.error(s"Request failed with: $err")
        }
    }
  }

  private def updateCurrentState(forecast: Forecast, thresholds: Map[CityName, Temperature]) = {
    val values = forecast.values.map { f =>
      val validated = thresholds.get(forecast.cityName) match {
        case Some(t) => TemperatureValidation(f.temperature, t).validate
        case None    => TemperatureValidation.Valid
      }
      StateValue(f.measuredAt, f.temperature, validated)
    }
    val exceeding = values collect { case x: StateValue if !x.isValid => x }
    if (exceeding.nonEmpty)
      logger.warn(s"Found exceeding values: $exceeding")
    state.update(forecast.cityName, values)
    context.system.eventStream.publish(StateOut(forecast.cityName, values))
  }
}

object ForecastMonitoringActor {
  def props(cache: ForecastCache): Props = Props(new ForecastMonitoringActor(cache))

  sealed trait Command
  case class Initialize(locations: List[Location]) extends Command
  case class ReceivedForecast(
    forecast: List[WeatherForecastApi.V[Forecast]],
    thresholds: Map[CityName, Temperature]) extends Command

  case class StateValue(
    measuredAt: Instant,
    temperature: Temperature,
    validated: TemperatureValidation.Result) {
    def isValid: Boolean = PartialFunction.cond(validated) {
      case Valid => true
    }
    override def toString: String = s"Measured: $measuredAt, temp: $temperature, validated: $validated"
  }

  sealed trait Out

  case class StateOut(cityName: CityName, values: List[StateValue]) extends Out
}
