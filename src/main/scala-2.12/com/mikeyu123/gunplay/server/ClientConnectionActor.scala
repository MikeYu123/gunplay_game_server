package com.mikeyu123.gunplay.server

import akka.actor.{Actor, ActorRef, Terminated}
import akka.http.scaladsl.model.ws.TextMessage
import com.mikeyu123.gunplay.utils.{ControlsParser, SpawnPool}
import com.mikeyu123.gunplay_physics.structs.{Point, Vector}
import spray.json._

/**
  * Created by mihailurcenkov on 25.07.17.
  */

case class Controls(up: Boolean, down: Boolean, left: Boolean, right: Boolean, angle: Double)
case object RegisterPlayer
case class Message(`type`: String, uuid: String, message: Option[JsValue])

class ClientConnectionActor(worldActor: ActorRef) extends Actor with DefaultJsonProtocol {
//  Todo: move to separate trait
  implicit val messageFormat = jsonFormat3(Message)
  implicit val controlsFormat = jsonFormat5(Controls)
  implicit val publishUpdatesFormat = jsonFormat2(PublishUpdates)
  //TODO possibly move connection to constructor to avoid Option handling
  var connection: Option[ActorRef] = None


  override def preStart(): Unit = {
    super.preStart()
    worldActor ! RegisterClient
  }

  val receive: Receive = {
//    Connection initialized
    case RegisterConnection(a: ActorRef) =>
      connection = Some(a)
      context.watch(a)

//      Connection terminated
    case Terminated(a) if connection.contains(a) =>
      connection = None
      context.stop(self)

//      Sink close callback
    case ConnectionClose =>
      context.stop(self)

//    Here we take incoming messages
//      TODO: check whether there might be binary messages
//      TODO: move deserializers to separate layer
    case TextMessage.Strict(t) =>
//controls update and registering here
//      connection.foreach(_ ! TextMessage.Strict(s"echo $t"))
      val json: Message = t.toJson.convertTo[Message]
      json.`type` match {
        case "controls" =>
          json.message.foreach { message =>
            val controls: Controls = message.convertTo[Controls]
            val (velocity: Vector, angle: Double) = ControlsParser.parseControls(controls)
            val messageToSend: UpdateControls = UpdateControls(json.uuid, velocity, angle)
            worldActor ! messageToSend
          }
        case "register" =>
          val spawnPoint: Point = SpawnPool.randomSpawn
          val messageToSend = AddPlayer(json.uuid, spawnPoint.x, spawnPoint.y)
          worldActor ! messageToSend
      }

    case message: PublishUpdates =>
      val json = message.toJson
    case _ => // ingore
  }

  override def postStop(): Unit = connection.foreach(context.stop)
}
