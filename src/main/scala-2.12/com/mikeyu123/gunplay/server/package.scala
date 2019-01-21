package com.mikeyu123.gunplay

import java.util.UUID

import akka.actor.ActorRef
import com.mikeyu123.gunplay.server.ClientConnectionActor.Updates
import com.mikeyu123.gunplay.server.WorldActor.LeaderboardEntry
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
  case object EmitBullet
  case object Step
  case object RegisterClient

  case object RegisterPlayer
}
