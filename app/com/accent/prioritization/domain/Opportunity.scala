package com.accent.prioritization.domain

import com.accent.prioritization.utils.EnumUtils
import play.api.libs.json.{Json, Reads}
import play.api.mvc.{PathBindable, QueryStringBindable}

object ScoreTypes extends Enumeration {
  val Attractiveness = Value

  implicit def scoreTypePathBinder(implicit stringBindable: QueryStringBindable[String]) = new PathBindable[ScoreTypes.Value] {
    override def bind(key: String, value: String): Either[String, ScoreTypes.Value] = Right(ScoreTypes.withName(value))

    override def unbind(key: String, value: ScoreTypes.Value): String = value.toString
  }
}

case class Opportunity(opportunityId: String, oppName: String, score: Int, scoreType: ScoreTypes.Value)

object Opportunity {
  implicit val scoreTypesFormat: Reads[ScoreTypes.Value] = EnumUtils.enumReads(ScoreTypes)
  implicit val oppFormat = Json.format[Opportunity]
}
