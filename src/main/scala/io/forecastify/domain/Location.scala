package io.forecastify.domain

import play.api.libs.json._

object Location {
  case class Location(cityName: CityName, threshold: Temperature) {
    override def toString: String = s"city: $cityName, threshold: $threshold"
  }
  case class CityName(value: String) extends AnyVal {
    override def toString: String = s"$value"
  }
  case class Temperature(value: BigDecimal) extends AnyVal {
    override def toString: String = s"$value"

  }

  implicit val CityFormat = new Format[CityName] {
    override def reads(json: JsValue): JsResult[CityName] = {
      for (value <- json.validate[String]) yield CityName(value)
    }
    override def writes(o: CityName): JsValue = JsString(o.value)
  }

  implicit val TemperatureFormat = new Format[Temperature] {
    override def reads(json: JsValue): JsResult[Temperature] = {
      for(value <- json.validate[BigDecimal]) yield Temperature(value)
    }
    override def writes(o: Temperature): JsValue = JsNumber(o.value)
  }

  implicit val LocationFormat = Json.format[Location]
}
