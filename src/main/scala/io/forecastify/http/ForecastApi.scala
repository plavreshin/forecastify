package io.forecastify.http

class ForecastApi extends BaseApi {
  lazy val routes = {
    logRequestResult("forecast-api") {
      pathPrefix("api" / "location") {
        get {
          complete("Implement locations response")
        }
      }
    }
  }
}

object ForecastApi {
  sealed trait UserRequest extends Product with Serializable

  object UserRequest {
    case class Initialize(locations: List[LocationReq]) extends UserRequest
    case class LocationReq(cityId: String, temperatures: Set[Int])
  }
}
