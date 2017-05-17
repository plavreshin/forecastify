package io.forecastify.api

import java.time.Instant

import com.typesafe.scalalogging.LazyLogging
import io.forecastify.api.WeatherForecastApi.{Forecast, V}
import io.forecastify.domain.Location.{CityName, Temperature}
import play.api.libs.json._

import scala.concurrent.Future

trait WeatherForecastApi extends LazyLogging {
  def fetchForecast(cityName: String): Future[V[Forecast]]
}

object WeatherForecastApi {
  type V[A] = Either[WeatherApiFailure, A]

  sealed trait WeatherApiFailure
  object WeatherApiFailure {
    case class MalformedRequest(reason: String) extends WeatherApiFailure
    case object NoResponse extends WeatherApiFailure
  }

  case class Forecast(id: Long, cityName: CityName, values: List[ForecastValue])
  case class ForecastValue(temperature: Temperature, measuredAt: Instant)

  implicit val forecastValueFormat: OFormat[ForecastValue] = {
    val writes = Json.writes[ForecastValue]
    val reads = new Reads[ForecastValue] {
      override def reads(json: JsValue): JsResult[ForecastValue] = {
        for {
          temp <- (json \ "main" \ "temp").validate[Temperature]
          measured <- (json \ "dt").validate[Long]
        } yield ForecastValue(temperature = temp, measuredAt = Instant.ofEpochSecond(measured))
      }
    }
    OFormat(reads, writes)
  }

  implicit val ForecastFormat: OFormat[Forecast] =  {
    val writes = Json.writes[Forecast]
    val reads = new Reads[Forecast] {
      override def reads(json: JsValue): JsResult[Forecast] = {
        for {
          cityName <- (json \ "city" \ "name").validate[CityName]
          id <- (json \ "city" \ "id").validate[Long]
          values <- (json \ "list").validate[List[ForecastValue]]
        } yield Forecast(id, cityName, values)
      }
    }
    OFormat(reads, writes)
  }
}
