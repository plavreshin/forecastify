package io.forecastify.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, StatusCodes, Uri}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshal}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
import io.forecastify.api.OpenWeatherMapApiClient.ApiKey
import io.forecastify.api.WeatherForecastApi.WeatherApiFailure.{MalformedRequest, NoResponse}
import io.forecastify.api.WeatherForecastApi.{Forecast, V, ForecastFormat}
import io.forecastify.domain.Location.CityName

import scala.concurrent.Future

class OpenWeatherMapApiClient()(private implicit val system: ActorSystem) extends WeatherForecastApi {

  private implicit val ec = system.dispatcher
  private implicit val mat = ActorMaterializer()

  def fetchForLocations(locations: List[CityName]): Future[List[V[Forecast]]] = {
    Future.sequence(locations.map(cityName => fetchForecast(cityName.value)))
  }

  override def fetchForecast(cityName: String): Future[V[Forecast]] = {
    request[Forecast](OpenWeatherMapApiClient.ForecastUri, queryParams(cityName))
  }

  private def request[T: FromEntityUnmarshaller](
    uri: Uri,
    query: Query) = {

    val req = HttpRequest(uri = uri.withQuery(query))
    for {
      r <- Http().singleRequest(req)
      f <- r.status match {
        case StatusCodes.OK         => Unmarshal(r.entity).to[T].map(x => Right(x))
        case StatusCodes.BadRequest => Unmarshal(r.entity).to[String].map(x => Left(MalformedRequest(x)))
        case other                  =>
          logger.error(s"Failed to retrieve forecast for query: $uri with params: $query, failure:", other)
          Future.successful(Left(NoResponse))
      }
    } yield f
  }

  private def queryParams(cityName: String) = Query(Map("q" -> cityName, "appid" -> ApiKey, "units" -> "metric"))
}

object OpenWeatherMapApiClient {
  lazy val ApiKey: String = config.getString("forecastify.openweathermap.key")
  lazy val ForecastUri: Uri = Uri(s"$uri/forecast")

  private val config = ConfigFactory.load()
  private val uri = config.getString("forecastify.openweathermap.uri")
}
