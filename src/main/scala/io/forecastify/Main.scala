package io.forecastify

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import io.forecastify.http.{ForecastApi, ForecastCache}

object Main extends App with LazyLogging {
  private val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("Forecastify")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  lazy val forecastCache = new ForecastCache

  lazy val forecastApi = new ForecastApi

  val (host, httpPort) = (config.getString("http.server.host"), config.getInt("http.server.port"))
  Http().bindAndHandle(forecastApi.routes, host, httpPort)
}
