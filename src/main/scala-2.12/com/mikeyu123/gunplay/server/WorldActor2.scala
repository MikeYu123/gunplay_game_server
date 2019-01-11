package com.mikeyu123.gunplay.server

import java.util.UUID

import akka.actor.{Actor, ActorRef, Terminated}
import com.mikeyu123.gunplay.objects._
import com.mikeyu123.gunplay.objects.huy.{Player, Scene}
import org.dyn4j.geometry.Vector2

/**
  * Created by mihailurcenkov on 25.07.17.
  */


class WorldActor2(val scene: Scene) extends Actor {
//  TODO: move to world constructor
  def this() = this(new Scene())
//  TODO: maybe mutable map?
  var clients: Map[ActorRef, UUID] = Map[ActorRef, UUID]()


  override def receive: Receive = {
    case AddPlayer(x, y) =>
      val s = sender()
      val player = new Player(position = new Vector2(x, y))
      clients += (s -> player.getId)
      context.watch(s)
      scene.addPlayer(player)
      s ! Registered(player.getId)
    case UpdateControls(velocity, angle) =>
//      TODO: handle shit when no uuid
      val s = sender()
      val uuid: UUID = clients(s)
      scene.updateControls(uuid, velocity, angle)
    case EmitBullet =>
      //      TODO: handle shit when no uuid
      val uuid: UUID = clients(sender())
      scene.emitBullet(uuid)
    case Step =>
      scene.step()
      clients.foreach { _._1 ! PublishUpdates(scene.updates.marshall) }
    case Terminated(client) =>
//      println(s"terminated ${clients(client)}")
//      TODO: remove body from world
      scene removePlayerById clients(client)
      clients -= client
    case _ =>
  }
}
