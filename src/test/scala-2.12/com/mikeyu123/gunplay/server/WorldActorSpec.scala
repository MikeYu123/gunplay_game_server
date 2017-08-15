package com.mikeyu123.gunplay.server

import akka.actor.{ActorSystem, PoisonPill}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.mikeyu123.gunplay.TestUtils
import com.mikeyu123.gunplay.objects.{Body, Bullet}
import com.mikeyu123.gunplay_physics.structs.Vector
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
  val sampleBody: Body = Body.initBody(dummyUuid, 50, -50)

  it should "add player to bodies" in {
    actorRef ! AddPlayer(dummyUuid, 50, -50)
    actor.world.players should contain (sampleBody)
  }

  it should "add player to clients" in {
    actor.clients should contain (self -> dummyUuid)
  }

  it should "update player correctly" in {
    actorRef ! UpdateControls(Vector(-1, 1), .5)
    val newBody = Body(sampleBody.uuid, sampleBody.graphicsObject, Vector(-1, 1), .5)
    actor.world.players should contain (newBody)
  }

  it should "invoke step correctly" in {
    schedulerProbe.send(actorRef, Step)
    val newBody = Body(sampleBody.uuid, sampleBody.graphicsObject.move(Vector(-1, 1)), Vector(-1, 1), .5)
    actor.world.players should contain (newBody)
  }

  it should "send message on step" in {
    val newBody = Body(sampleBody.uuid, sampleBody.graphicsObject.move(Vector(-1, 1)), Vector(-1, 1), .5)
    expectMsg(PublishUpdates(Set[Body](newBody), Set[Bullet]()))
  }

  it should "remove client on termination" in {
//    TODO: is it even representative?
    self ! PoisonPill
    actor.clients shouldNot contain (self -> dummyUuid)
  }
}
