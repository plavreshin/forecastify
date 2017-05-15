package io.forecastify.api

import com.typesafe.scalalogging.LazyLogging
import io.forecastify.api.WeatherForecastApi.Forecast.{Current, LongTerm}
import io.forecastify.api.WeatherForecastApi.V
import play.api.libs.json._

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

trait WeatherForecastApi extends LazyLogging {
  def fetchCurrentWeather(cityName: String): Future[V[Current]]

  def fetchPeriodWeather(cityName: String, periodLength: FiniteDuration): Future[V[LongTerm]]
}

object WeatherForecastApi {
  type V[A] = Either[WeatherApiFailure, A]

  sealed trait WeatherApiFailure
  object WeatherApiFailure {
    case class MalformedRequest(reason: String) extends WeatherApiFailure
    case object NoResponse extends WeatherApiFailure
  }

  sealed trait Forecast {
    def id: Long
    def name: String
    def cod: Int
  }
  object Forecast {
    case class Current(id: Long, name: String, cod: Int) extends Forecast
    case class LongTerm(id: Long, name: String, cod: Int) extends Forecast

    implicit val CurrentFormat = Json.format[Current]
    implicit val LongTermFormat = Json.format[LongTerm]
//    implicit val Format: Format[Forecast] = new Format[Forecast] {
//      override def reads(json: JsValue): JsResult[Forecast] = JsSuccess(Current(id = "", name = ""))
//      override def writes(o: Forecast): JsValue = Json.obj()
//    }
  }
}
