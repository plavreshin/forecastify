package io.forecastify.actor

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import io.forecastify.actor.ForecastMonitoringActor.{Command, Initialize, ScheduleRefresh}
import io.forecastify.http.ForecastApi.UserRequest.LocationReq

class ForecastMonitoringActor extends Actor with LazyLogging {
  override def receive: Receive = {
    case c: Command => c match {
      case x: Initialize   =>
      case ScheduleRefresh =>
    }
  }
}

object ForecastMonitoringActor {
  sealed trait Command
  case class Initialize(locations: LocationReq) extends Command
  case object ScheduleRefresh extends Command
}
