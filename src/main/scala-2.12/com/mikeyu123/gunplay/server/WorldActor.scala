package com.mikeyu123.gunplay.server

import akka.actor.{Actor, ActorRef, Terminated}
import com.mikeyu123.gunplay.objects._
import com.mikeyu123.gunplay_physics.structs.Vector

/**
  * Created by mihailurcenkov on 25.07.17.
  */
case class AddPlayer(uuid: String, x: Double, y: Double)
case class UpdateControls(uuid: String, velocity: Vector, angle: Double)
case object Step
case class PublishUpdates(bodies: Set[Body], bullets: Set[Bullet])
case object RegisterClient

class WorldActor(var world: World) extends Actor {
  def this() = this(World(Set[Body](), Set[Bullet](), Set[Wall](), Set[Door]()))
//  TODO: maybe mutable map?
  var clients: Set[ActorRef] = Set[ActorRef]()


  override def receive: Receive = {
    case AddPlayer(uuid, x, y) =>
      world = world.addPlayer(Body.initBody(uuid, x, y))
    case UpdateControls(uuid, velocity, angle) =>
      world = world.updateControls(uuid, velocity, angle)
    case Step =>
      world = world.step
      clients.foreach { _ ! PublishUpdates(world.players, world.bullets) }
    case Terminated(client) =>
      clients -= client
//      TODO don't know whether we need UUIDs here
    case RegisterClient =>
      val sender = sender()
      clients += sender
      context.watch(sender)
    case _ =>
  }
}
