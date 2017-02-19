package com.accent.prioritization.utils

import akka.stream.scaladsl.Source
import play.api.libs.json.OWrites

object JsonStreams {
  def streamAsJsonArray[T](source: Source[T, _])(implicit writer: OWrites[T]): Source[String, _] = {
    val elementJsons = source.map(element => writer.writes(element).toString())
    elementJsons.intersperse("[", ",", "]")
  }
}
