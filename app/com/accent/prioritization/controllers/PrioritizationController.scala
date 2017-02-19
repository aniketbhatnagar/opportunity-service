package com.accent.prioritization.controllers

import com.accent.prioritization.domain.ScoreTypes
import com.accent.prioritization.services.PrioritizationService
import com.accent.prioritization.utils.JsonStreams
import com.google.inject.{Inject, Singleton}
import play.api.mvc.{Action, Controller}

@Singleton
class PrioritizationController @Inject() (val prioritizationService: PrioritizationService) extends Controller {
  def getTopPerformanceOpps(scoreType: ScoreTypes.Value, limit: Int) = Action { implicit request =>
    val opportunities = prioritizationService.getTopPerformanceOpps(scoreType, limit)
    Ok.chunked(JsonStreams.streamAsJsonArray(opportunities))
  }
}
