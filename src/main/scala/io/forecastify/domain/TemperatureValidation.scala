package io.forecastify.domain

import io.forecastify.domain.Location.Temperature
import io.forecastify.domain.TemperatureValidation.{Exceeded, Result, Valid}

import scala.math.BigDecimal.RoundingMode

case class TemperatureValidation(current: Temperature, threshold: Temperature) {
  def validate: Result =
    if (current.value <= threshold.value)
      Valid
    else
      Exceeded((100 - (threshold.value / current.value * 100)).setScale(2, RoundingMode.HALF_EVEN))
}

object TemperatureValidation {
  sealed trait Result extends Product with Serializable
  case class Exceeded(percentage: BigDecimal) extends Result {
    override def toString: String = s"Exceeded by percentage: $percentage"
  }
  case object Valid extends Result {
    override def toString: String = s"Valid"
  }
}
