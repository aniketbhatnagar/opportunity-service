package com.accent.prioritization.services

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.accent.prioritization.domain.{Opportunity, ScoreTypes}
import com.google.inject.{Inject, Singleton}

@Singleton
class PrioritizationService @Inject() (val prioritizationRepository: PrioritizationRepository) {
  def getTopPerformanceOpps(scoreType: ScoreTypes.Value, limit: Int): Source[Opportunity, NotUsed] = {
    prioritizationRepository.getOpportunitiesSortedByScore(scoreType, limit)
  }
}
