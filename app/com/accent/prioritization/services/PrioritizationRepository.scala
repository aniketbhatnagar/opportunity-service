package com.accent.prioritization.services

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.accent.prioritization.domain.{Opportunity, ScoreTypes}
import com.google.inject.{ImplementedBy, Inject, Singleton}

import scala.collection.mutable
import scala.concurrent.Future

@ImplementedBy(classOf[MockPrioritizationRepository])
trait PrioritizationRepository {
  def getOpportunitiesSortedByScore(scoreType: ScoreTypes.Value, limit: Int): Source[Opportunity, NotUsed]

  def addOpportunity(opportunity: Opportunity): Future[Unit]
}

@Singleton
class MockPrioritizationRepository @Inject() extends PrioritizationRepository {

  import scala.concurrent.ExecutionContext.Implicits.global

  private val opportunities = mutable.HashSet[Opportunity]()

  override def getOpportunitiesSortedByScore(scoreType: ScoreTypes.Value, limit: Int): Source[Opportunity, NotUsed] = {
    val sortedOpportunities = opportunities.toList.sorted(Ordering.by[Opportunity, Int](_.score).reverse)
                                           .dropRight(opportunities.size - limit)
    Source(sortedOpportunities)
  }

  override def addOpportunity(opportunity: Opportunity): Future[Unit] = {
    opportunities.add(opportunity)
    Future.successful(Unit)
  }
}
