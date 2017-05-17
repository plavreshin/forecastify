package io.forecastify

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import io.forecastify.actor.ForecastMonitoringActor
import io.forecastify.actor.ForecastMonitoringActor.Initialize
import io.forecastify.domain.Location.Location
import io.forecastify.http.{ForecastApi, ForecastCache}
import play.api.libs.json.Json

import scala.io.Source
import scala.util.Try

object Main extends App with LazyLogging {
  private val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("Forecastify")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  lazy val locations = for {
    is <- Try(getClass.getResourceAsStream("/locations.json")).toOption
    f <- Try(Source.fromInputStream(is).getLines().mkString).toOption
    lcs <- (for {
      json <- Json.parse(f).validate[List[Location]]
    } yield json).asOpt
  } yield lcs

  lazy val cache = new ForecastCache(system)

  lazy val api = new ForecastApi(cache)

  val actor = system.actorOf(ForecastMonitoringActor.props(cache))
  for (lcs <- locations) { actor ! Initialize(lcs) }

  val (host, httpPort) = (config.getString("http.server.host"), config.getInt("http.server.port"))
  Http().bindAndHandle(api.routes, host, httpPort)
}
