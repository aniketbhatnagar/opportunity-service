package com.accent.prioritization

import java.nio.charset.Charset

import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import com.accent.prioritization.controllers.PrioritizationController
import com.accent.prioritization.domain.{Opportunity, ScoreTypes}
import com.accent.prioritization.services.PrioritizationRepository
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

class ServiceSpec extends PlaySpec with OneAppPerTest {

  private implicit val system = ActorSystem("ServiceSpec")
  private implicit val materializer = ActorMaterializer()

  "Service" should {
    "returns empty json array when no opportunities exist" in {
      val controller = app.injector.instanceOf[PrioritizationController]
      val responseFuture = controller.getTopPerformanceOpps(ScoreTypes.Attractiveness, 10).apply(FakeRequest())
      val opportunities = parseOpportunities(responseFuture)
      opportunities.size mustBe 0
    }

    "returns json array containing single opportunity when only 1 opportunity exists" in {
      val controller = app.injector.instanceOf[PrioritizationController]
      val opp1 = Opportunity("1", "opp1", 10, ScoreTypes.Attractiveness)
      addOpportunities(opp1)
      val responseFuture = controller.getTopPerformanceOpps(ScoreTypes.Attractiveness, 10).apply(FakeRequest())
      val opportunities = parseOpportunities(responseFuture)
      opportunitiesMustBe(opportunities, opp1)
    }

    "returns json array containing sorted opportunities when opportunities are present in repository" in {
      val repository = app.injector.instanceOf[PrioritizationRepository]
      val controller = app.injector.instanceOf[PrioritizationController]
      val opp1 = Opportunity("1", "opp1", 10, ScoreTypes.Attractiveness)
      val opp2 = Opportunity("2", "opp2", 90, ScoreTypes.Attractiveness)
      addOpportunities(opp1, opp2)
      val responseFuture = controller.getTopPerformanceOpps(ScoreTypes.Attractiveness, 10).apply(FakeRequest())
      val opportunities = parseOpportunities(responseFuture)
      opportunitiesMustBe(opportunities, opp2, opp1)
    }

    "returns json array containing 3 opportunities when 5 opportunities are present in repository but API is requested to limit opportunities to 3" in {
      val controller = app.injector.instanceOf[PrioritizationController]
      val opp1 = Opportunity("1", "opp1", 10, ScoreTypes.Attractiveness)
      val opp2 = Opportunity("2", "opp2", 90, ScoreTypes.Attractiveness)
      val opp3 = Opportunity("3", "opp3", 40, ScoreTypes.Attractiveness)
      val opp4 = Opportunity("4", "opp4", 20, ScoreTypes.Attractiveness)
      val opp5 = Opportunity("5", "opp5", 50, ScoreTypes.Attractiveness)
      addOpportunities(opp1, opp2, opp3, opp4, opp5)
      val responseFuture = controller.getTopPerformanceOpps(ScoreTypes.Attractiveness, 3).apply(FakeRequest())
      val opportunities = parseOpportunities(responseFuture)
      opportunitiesMustBe(opportunities, opp2, opp5, opp3)
    }
  }

  private def opportunitiesMustBe(actualOpportunities: Array[Opportunity], expectedOpportunities: Opportunity*): Unit = {
    actualOpportunities.size mustBe expectedOpportunities.size
    for ((expectedOportunity, index) <- expectedOpportunities.zipWithIndex) {
      actualOpportunities(index) mustBe expectedOportunity
    }
  }

  private def parseOpportunities(responseFuture: Future[Result]) = {
    val responseJson = parseJsonFromResponse(responseFuture)
    val opportunities = responseJson.as[Array[Opportunity]]
    opportunities
  }

  private def parseJsonFromResponse(responseFuture: Future[Result]) = {
    val response = Await.result(responseFuture, Duration.Inf)
    val body = Await.result(response.body.consumeData, Duration.Inf)
    val bodyStr = body.decodeString(Charset.defaultCharset())
    val parsedBody = Json.parse(bodyStr)
    parsedBody
  }

  private def addOpportunities(opportunities: Opportunity*): Unit = {
    val repository = app.injector.instanceOf[PrioritizationRepository]
    for (opportunity <- opportunities) {
      repository.addOpportunity(opportunity)
    }
  }
}
