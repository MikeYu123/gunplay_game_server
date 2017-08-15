package com.mikeyu123.gunplay.server

import akka.actor.{Actor, ActorRef, Terminated}
import com.mikeyu123.gunplay.objects._

/**
  * Created by mihailurcenkov on 25.07.17.
  */


class WorldActor(var world: World) extends Actor {
//  TODO: move to world constructor
  def this() = this(World(Set[Body](), Set[Bullet](), Set[Wall](), Set[Door]()))
//  TODO: maybe mutable map?
  var clients: Map[ActorRef, String] = Map[ActorRef, String]()


  override def receive: Receive = {
    case AddPlayer(uuid, x, y) =>
      val s = sender()
      clients += (s -> uuid)
      context.watch(s)
      world = world.addPlayer(Body.initBody(uuid, x, y))
    case UpdateControls(velocity, angle) =>
//      TODO: handle shit when no uuid
      val uuid: String = clients(sender())
      world = world.updateControls(uuid, velocity, angle)
    case Step =>
      world = world.step
      clients.foreach { _._1 ! PublishUpdates(world.players, world.bullets) }
    case Terminated(client) =>
//      TODO: remove body from world
      clients -= client
    case _ =>
  }
}
