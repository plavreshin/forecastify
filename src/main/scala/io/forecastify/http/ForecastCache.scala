package io.forecastify.http

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.scalalogging.LazyLogging
import io.forecastify.actor.ForecastMonitoringActor
import io.forecastify.actor.ForecastMonitoringActor.StateValue
import io.forecastify.domain.Location.CityName

import scala.collection.concurrent.TrieMap

class ForecastCache(system: ActorSystem) extends LazyLogging {
  private val cityForecast: TrieMap[CityName, List[StateValue]] = TrieMap.empty[CityName, List[StateValue]]

  system.actorOf(Props(new Listener()))

  def get(cityName: CityName): Option[List[StateValue]] = cityForecast.get(cityName)

  private class Listener extends Actor {
    override def receive: Receive = {
      case x: ForecastMonitoringActor.Out => x match {
        case s: ForecastMonitoringActor.StateOut => cityForecast.put(s.cityName, s.values)
      }
    }
    override def preStart(): Unit = {
      context.system.eventStream.subscribe(self, classOf[ForecastMonitoringActor.Out])
    }
  }

}

object ForecastCache {
  case class ThresholdWarning(exceededByPercentage: BigDecimal)
}
