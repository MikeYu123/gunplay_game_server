package com.mikeyu123.gunplay.server

import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.zip.GZIPOutputStream

import akka.actor.{Actor, ActorRef, Terminated}
import akka.http.scaladsl.model.ws.BinaryMessage
import akka.http.scaladsl.model.ws.TextMessage
import akka.util.ByteString
import com.mikeyu123.gunplay.server.ClientConnectionActor._
import com.mikeyu123.gunplay.server.WorldActor.LeaderboardEntry
import com.mikeyu123.gunplay.server.messaging._
import com.mikeyu123.gunplay.utils
import spray.json._
import com.mikeyu123.gunplay.utils.{ControlsParser, SpawnPool, Vector2}
import scala.util.Try

/**
  * Created by mihailurcenkov on 25.07.17.
  */
object ClientConnectionActor {
  val defaultName = utils.AppConfig.getString("leaderboard.defaultName")

  sealed trait ServerMessage
  case class Registered(id: UUID) extends ServerMessage
  case class Updates(
              bodies: Set[PlayerObject],
              bullets: Set[MessageObject],
              doors: Set[MessageObject],
              drops: Set[DropObject],
              player: Option[PlayerObject] = None) extends ServerMessage
  case class Leaderboard(entries: Seq[LeaderboardEntry] = Seq()) extends ServerMessage


  sealed trait ClientMessage
  case class Register(name: Option[String] = None) extends ClientMessage
  case class Controls(up: Boolean,
                      down: Boolean,
                      left: Boolean,
                      right: Boolean,
                      angle: Double,
                      click: Boolean,
                      space: Boolean) extends ClientMessage

  sealed trait ConnectionType
  case object BinaryConnection extends ConnectionType
  case object JsonConnection extends ConnectionType

}
class ClientConnectionActor(worldActor: ActorRef) extends Actor with BinaryProtocol with JsonProtocol {
  //TODO possibly move connection to constructor to avoid Option handling
  var connection: Option[ActorRef] = None

  var connectionType: Option[ConnectionType] = None

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
//      TODO handle exceptions
    case TextMessage.Strict(t) =>
      val message: ClientMessage = t.parseJson.convertTo[ClientMessage]
      message match {
        case controls: Controls =>
//            TODO this fails if message is broken
          val (velocity, angle, click, space) = ControlsParser.parseControls(controls)
          val messageToSend: UpdateControls = UpdateControls(velocity, angle, click)
          worldActor ! messageToSend
          if (space)
            worldActor ! DropWeapon
        case Register(name) =>
//          TODO REWORK THIS WHOLE
          connectionType = Some(JsonConnection)
          val messageToSend = AddPlayer(name.getOrElse(ClientConnectionActor.defaultName))
          worldActor ! messageToSend
      }

    case BinaryMessage.Strict(t) =>
      val buffer = t.asByteBuffer
      val message: ClientMessage = buffer.convertTo[ClientMessage]
      message match {
        case controls: Controls =>
          //            TODO this fails if message is broken
          val (velocity, angle, click, space) = ControlsParser.parseControls(controls)
          val messageToSend: UpdateControls = UpdateControls(velocity, angle, click)
          worldActor ! messageToSend
          if (space)
            worldActor ! DropWeapon
        case Register(name) =>
          //          TODO REWORK THIS WHOLE
          connectionType = Some(BinaryConnection)
          val messageToSend = AddPlayer(name.getOrElse(ClientConnectionActor.defaultName))
          worldActor ! messageToSend
      }

    case message: ServerMessage =>
      connection foreach { conn =>
        val messageToSend = message.toJson.toString()
//        println(message)
//        TODO split logic onto binary/text
        connectionType match {
          case Some(BinaryConnection) =>
            conn ! BinaryMessage.Strict (ByteString (message.toBinary.array) )
          case Some(JsonConnection) =>
            conn ! TextMessage.Strict(messageToSend)
        }
      }

    case _ => // ingore
  }

  override def postStop(): Unit = connection.foreach(context.stop)
}
