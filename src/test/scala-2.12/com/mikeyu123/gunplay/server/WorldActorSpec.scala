package com.mikeyu123.gunplay.server

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.mikeyu123.gunplay.TestUtils
import com.mikeyu123.gunplay.objects.Body
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

/**
  * Created by mihailurcenkov on 14.08.17.
  */
class WorldActorSpec extends TestKit(ActorSystem()) with ImplicitSender with
  FlatSpecLike with BeforeAndAfterAll with Matchers with TestUtils {

//  TODO: DRY via trait inheritance
  override def afterAll = {
    TestKit.shutdownActorSystem(system)
  }

  val schedulerProbe = TestProbe()
  val actorRef = TestActorRef(new WorldActor())
  val actor: WorldActor = actorRef.underlyingActor

  it should "add player correctly" in {
    actorRef ! AddPlayer(dummyUuid, 50, -50)
    val body = Body.initBody(dummyUuid, 50, -50)
    actor.world.players should contain (body)
  }
}
