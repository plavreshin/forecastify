package io.forecastify.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.Uri.Path.Segment
import io.forecastify.actor.ForecastMonitoringActor.StateValue
import io.forecastify.domain.Location.CityName
import play.api.libs.json.{JsValue, Json, Writes}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

class ForecastApi(cache: ForecastCache) extends BaseApi {
  import ForecastApi.JsonFormat

  lazy val routes = cors() {
    logRequestResult("forecast-api") {
      pathPrefix("api" / "location") {
        (path(Segment) & get) { city =>
          cache.get(CityName(city.capitalize)) match {
            case Some(st) => complete(st)
            case None => complete(StatusCodes.BadRequest, s"Incorrect $city supplied")
          }
        }
      }
    }
  }
}

object ForecastApi {
  implicit val JsonFormat: Writes[StateValue] = new Writes[StateValue] {
    override def writes(o: StateValue): JsValue = Json.obj(
      "measuredAt" -> o.measuredAt,
      "temperature" -> o.temperature,
      "validated" -> o.validated.productPrefix)
  }
}
