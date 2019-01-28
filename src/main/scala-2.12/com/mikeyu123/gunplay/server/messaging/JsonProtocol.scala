package com.mikeyu123.gunplay.server.messaging

import java.util.UUID

import com.mikeyu123.gunplay.server.ClientConnectionActor._
import com.mikeyu123.gunplay.server.WorldActor.LeaderboardEntry
import com.mikeyu123.gunplay.server._
import spray.json.{DefaultJsonProtocol, JsObject, JsString, JsValue, JsonFormat, deserializationError, _}

import scala.collection.Map

/**
  * Created by mihailurcenkov on 06.08.17.
  */
trait JsonProtocol extends DefaultJsonProtocol with UuidMarshalling {
  implicit object RegisteredMessageFormat extends JsonFormat[Registered] {
    val defaultFormat = jsonFormat1(Registered)

    def write(registered: Registered) = JsObject("type" -> JsString("registered"), "id" -> UuidJsonFormat.write(registered.id))

    def read(value: JsValue) = defaultFormat.read(value)
  }

  implicit val messageObjectFormat = jsonFormat5(MessageObject)
  implicit val dropObjectFormat = jsonFormat3(DropObject)
  implicit val playerObjectFormat = jsonFormat7(PlayerObject)

  implicit object UpdatesMessageFormat extends JsonFormat[Updates] {
    val setFormat = implicitly[JsonFormat[Set[MessageObject]]]
    val playerSetFormat = implicitly[JsonFormat[Set[PlayerObject]]]
    val dropsSetFormat = implicitly[JsonFormat[Set[DropObject]]]
//    TODO make it work
//    val optionFormat = implicitly[JsonFormat[Option[MessageObject]]]
    val defaultFormat = jsonFormat5(Updates)
    def write(updates: Updates) = {
      JsObject("type" -> JsString("updates"),
        "bodies" -> playerSetFormat.write(updates.bodies),
        "bullets" -> setFormat.write(updates.bullets),
        "doors" -> setFormat.write(updates.doors),
        "drops" -> dropsSetFormat.write(updates.drops),
        "player" -> updates.player.fold[JsValue](JsNull)(playerObjectFormat.write)
      )
    }
    def read(value: JsValue) = defaultFormat.read(value)
  }

  implicit val leaderboardEntryFormat = jsonFormat4(LeaderboardEntry)

  implicit object LeaderboardFormat extends JsonFormat[Leaderboard] {
    val entrySeqFormat = implicitly[JsonFormat[Seq[LeaderboardEntry]]]
    val defaultFormat = jsonFormat(Leaderboard, "leaderboard")
    def write(leaderboard: Leaderboard) = {
      JsObject("type" -> JsString("leaderboard"),
        "leaderboard" -> entrySeqFormat.write(leaderboard.entries))
    }

    def read(value: JsValue) = defaultFormat.read(value)
  }

  implicit object ServerMessageFormat extends JsonFormat[ServerMessage] {
    override def read(json: JsValue): ServerMessage = json match {
      case JsObject(fields) => fields.get("type") match {
        case Some(JsString("registered")) => RegisteredMessageFormat.read(json)
        case Some(JsString("updates")) => UpdatesMessageFormat.read(json)
        case Some(JsString("leaderboard")) => LeaderboardFormat.read(json)
        case x => deserializationError(s"Expected valid server message, but got $x")
      }
      case x => deserializationError(s"Expected valid server message, but got $x")
    }

    override def write(obj: ServerMessage): JsValue = obj match {
      case x: Registered => RegisteredMessageFormat.write(x)
      case x: Leaderboard => LeaderboardFormat.write(x)
      case x: Updates => UpdatesMessageFormat.write(x)
    }
  }

  implicit object ControlsFormat extends JsonFormat[Controls] {
    val defaultFormat = jsonFormat6(Controls)
    def read(json: JsValue): Controls = defaultFormat.read(json)
    def write(controls: Controls) = {
      JsObject(
        "type" -> JsString("controls"),
        "up" -> JsBoolean(controls.up),
        "down" -> JsBoolean(controls.down),
        "right" -> JsBoolean(controls.right),
        "left" -> JsBoolean(controls.left),
        "click" -> JsBoolean(controls.click),
        "angle" -> JsNumber(controls.angle)
      )
    }
  }

  implicit object RegisterFormat extends JsonFormat[Register] {
    val defaultFormat = jsonFormat1(Register)
    def read(json: JsValue): Register = defaultFormat.read(json)
    def write(register: Register) = {
      JsObject(
        "type" -> JsString("register"),
        "name" -> register.name.fold[JsValue](JsNull)(JsString(_))
      )
    }
  }

  implicit object ClientMessageFormat extends JsonFormat[ClientMessage] {
    override def read(json: JsValue): ClientMessage = json match {
      case JsObject(fields) => fields.get("type") match {
        case Some(JsString("controls")) => ControlsFormat.read(json)
        case Some(JsString("register")) => RegisterFormat.read(json)
        case x => deserializationError(s"Expected valid client message, but got $x")
      }
      case x => deserializationError(s"Expected valid client message, but got $x")
    }

    override def write(obj: ClientMessage): JsValue = obj match {
      case x: Controls => ControlsFormat.write(x)
      case x: Register => RegisterFormat.write(x)
    }
  }

}
