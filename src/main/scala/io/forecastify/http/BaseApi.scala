package io.forecastify.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.http.scaladsl.server.{Directives, StandardRoute}
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

trait BaseApi extends Directives
  with PlayJsonSupport
  with DebuggingDirectives
  with LazyLogging {

  def respondWithSuccess(f: => Unit): StandardRoute = {
    f
    complete(StatusCodes.OK)
  }

  def respondWithFailure(t: Throwable): StandardRoute = {
    complete(t.getMessage)
  }
}
