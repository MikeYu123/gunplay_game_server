package com.mikeyu123.gunplay.server

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.ws.TextMessage
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.mikeyu123.gunplay.objects.{Body, Bullet}
import com.mikeyu123.gunplay_physics.structs.Vector
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

/**
  * Created by mihailurcenkov on 06.08.17.
  */

class ClientConnectionActorSpec extends TestKit(ActorSystem()) with ImplicitSender with
  FlatSpecLike with BeforeAndAfterAll with Matchers {

  override def afterAll = {
    TestKit.shutdownActorSystem(system)
  }

  val registerMessage = """{ "type": "register", "uuid": "13d9b6e2-96c8-4f9d-b664-15fb48658f8e" }"""
  val updateMessage =
    """{ "type": "controls", "uuid": "b3c06c5b-81ac-480d-a942-6c49ccf7ed74",
      |"message": {"up": false, "down": true, "left": false, "right": true, "angle": 0.5 } }""".stripMargin

  val worldProbe = TestProbe()
  val actor = system.actorOf(Props(classOf[ClientConnectionActor], worldProbe.ref))
  actor ! RegisterConnection(self)


  it should "register user correctly" in {
    actor ! TextMessage.Strict(registerMessage)
    //    TODO: rework after reworking spawnPool
    worldProbe.expectMsgPF() {
      case AddPlayer("13d9b6e2-96c8-4f9d-b664-15fb48658f8e", _, _) => true
    }
    worldProbe.expectNoMsg
  }

  it should "recieve controls message correctly" in {
    worldProbe.ignoreMsg {
      case x: AddPlayer => true
    }
    actor ! TextMessage.Strict(registerMessage)
    actor ! TextMessage.Strict(updateMessage)

//    TODO: fix after reworking controlsParser
    worldProbe.expectMsg(UpdateControls(Vector(2, 2), .5d))

    worldProbe.ignoreNoMsg
    worldProbe.expectNoMsg
  }


  it should "send updates correctly" in {
    actor ! TextMessage.Strict(registerMessage)
    //    TODO: rework after reworking spawnPool
    val (x: Double, y: Double) =  worldProbe.expectMsgPF() {
      case AddPlayer("13d9b6e2-96c8-4f9d-b664-15fb48658f8e", x, y) => (x, y)
      case _ => (0, 0)
    }

    val body = Body.initBody("13d9b6e2-96c8-4f9d-b664-15fb48658f8e", x, y)

    worldProbe.send(actor, PublishUpdates(Set[Body](body), Set[Bullet]()))

    val expectedMsg = s"""{"bodies":[{"uuid":"13d9b6e2-96c8-4f9d-b664-15fb48658f8e","x":$x,"y":$y,"angle":0.0}],"bullets":[]}"""

//    TODO: try to make things less dependent on JSON-serialization protocol
    expectMsgPF() {
      case TextMessage.Strict(expectedMsg) => true
    }
    worldProbe.expectNoMsg
  }
}
