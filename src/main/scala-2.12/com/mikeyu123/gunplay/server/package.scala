package com.mikeyu123.gunplay

import java.util.UUID

import akka.actor.ActorRef
import com.mikeyu123.gunplay.server.WorldActor.LeaderBoardEntry
import com.mikeyu123.gunplay.server.messaging.MessageObject
import com.mikeyu123.gunplay.utils.Vector2
import com.mikeyu123.gunplay_physics.structs.Vector
import spray.json.JsValue

/**
  * Created by mihailurcenkov on 06.08.17.
  */
//TODO add messages spec
package object server {
//  Callback message when connection is terminated
  case object ConnectionClose
//  Message on registering new connection
  case class RegisterConnection(connection: ActorRef)
// [[ClientConnectionActor]] sent message to [WorldActor] to add Body object
//  TODO: point??
  case class AddPlayer(name: String)
// [[ClientConnectionActor]] sent message to [WorldActor] to modify body controls
  case class UpdateControls(velocity: Vector2, angle: Double, click: Boolean)
  case class Registered(uuid: UUID)
  case object EmitBullet
  case object Step
  case class PublishUpdates(updates: Updates)
  case object RegisterClient

  case class Controls(up: Boolean, down: Boolean, left: Boolean, right: Boolean, angle: Double, click: Boolean)
  case object RegisterPlayer
  case class ClientMessage(`type`: String, message: Option[JsValue])
  case class Updates(bodies: Set[MessageObject], bullets: Set[MessageObject], doors: Set[MessageObject], leaderBoard: Seq[LeaderBoardEntry] = Seq())
}
