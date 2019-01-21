package com.mikeyu123.gunplay.server

import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.zip.GZIPOutputStream

import akka.actor.{Actor, ActorRef, Terminated}
import akka.http.scaladsl.model.ws.BinaryMessage
import akka.http.scaladsl.model.ws.TextMessage
import akka.util.ByteString
import com.mikeyu123.gunplay.server.ClientConnectionActor.{ClientMessage, Controls, Register, ServerMessage}
import com.mikeyu123.gunplay.server.WorldActor.LeaderboardEntry
import com.mikeyu123.gunplay.server.messaging.{BinaryProtocol, JsonProtocol, MessageObject, ObjectsMarshaller}
import com.mikeyu123.gunplay.utils.{ControlsParser, SpawnPool, Vector2}
import com.mikeyu123.gunplay_physics.structs.{Point, Vector}
import spray.json._

import scala.util.Try

/**
  * Created by mihailurcenkov on 25.07.17.
  */
object ClientConnectionActor {

  sealed trait ServerMessage
  case class Registered(id: UUID) extends ServerMessage
  case class Updates(
              bodies: Set[MessageObject],
              bullets: Set[MessageObject],
              doors: Set[MessageObject],
              player: Option[MessageObject] = None) extends ServerMessage
  case class Leaderboard(entries: Seq[LeaderboardEntry] = Seq()) extends ServerMessage


  sealed trait ClientMessage
  case class Register(name: Option[String] = None) extends ClientMessage
  case class Controls(up: Boolean,
                      down: Boolean,
                      left: Boolean,
                      right: Boolean,
                      angle: Double,
                      click: Boolean) extends ClientMessage

}
class ClientConnectionActor(worldActor: ActorRef) extends Actor with BinaryProtocol with JsonProtocol {
  //TODO possibly move connection to constructor to avoid Option handling
  var connection: Option[ActorRef] = None

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
      val message: ClientMessage = t.parseJson.convertTo[ClientMessage]
      message match {
        case controls: Controls =>
//            TODO this fails if message is broken
            val (velocity: Vector2, angle: Double, click: Boolean) = ControlsParser.parseControls(controls)
            val messageToSend: UpdateControls = UpdateControls(velocity, angle, click)
            worldActor ! messageToSend
        case Register(name) =>
//          TODO REWORK THIS WHOLE
          val messageToSend = AddPlayer(name.getOrElse("huy"))
          worldActor ! messageToSend
      }

    case message: ServerMessage =>
      connection foreach { conn =>
        val messageToSend = message.toJson.toString()
        conn ! BinaryMessage.Strict(message.toBinary)
        conn ! TextMessage.Strict(messageToSend)
      }

    case _ => // ingore
  }

  override def postStop(): Unit = connection.foreach(context.stop)
}
