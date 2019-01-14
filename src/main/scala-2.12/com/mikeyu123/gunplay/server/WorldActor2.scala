package com.mikeyu123.gunplay.server

import java.util.UUID

import akka.actor.{Actor, ActorRef, Terminated}
import com.mikeyu123.gunplay.objects._
import com.mikeyu123.gunplay.objects.huy.Scene.Murder
import com.mikeyu123.gunplay.objects.huy.{Player, Scene}
import com.mikeyu123.gunplay.server.WorldActor.LeaderBoardEntry
import com.mikeyu123.gunplay.utils.SpawnPool
import com.mikeyu123.gunplay_physics.structs.Point
import org.dyn4j.geometry.Vector2

/**
  * Created by mihailurcenkov on 25.07.17.
  */

object WorldActor {
  case class LeaderBoardEntry(id: UUID = UUID.randomUUID, name: String = "", kills: Int = 0, deaths: Int = 0)
}

class WorldActor2(val scene: Scene) extends Actor {
//  TODO: move to world constructor
  def this() = this(new Scene())
//  TODO: maybe mutable map?
  var clients: Map[ActorRef, UUID] = Map[ActorRef, UUID]()
  var bodies: Map[UUID, UUID] = Map()
  var leaderBoard: Map[UUID, LeaderBoardEntry] = Map()

  def processMurders(murders: Set[Murder]) = {
    murders.foreach(murder => {
      val killerId = bodies.find(_._2.equals(murder.killer)).map(_._1)
      val victimId = bodies.find(_._2.equals(murder.victim)).map(_._1)
      for {
        id <- killerId
        entry <- leaderBoard.get(id)
      } leaderBoard += (id -> entry.copy(kills = entry.kills + 1))
      for {
        id <- victimId
        entry <- leaderBoard.get(id)
      } {
        leaderBoard += (id -> entry.copy(deaths = entry.deaths + 1))
        bodies -= id
      }
    })
  }

  override def receive: Receive = {
    case AddPlayer(name, x, y) =>
      val s = sender()
      val player = new Player(position = new Vector2(x, y))
      val leaderBoardEntry = LeaderBoardEntry(name = name)
      clients += (s -> leaderBoardEntry.id)
      leaderBoard += (leaderBoardEntry.id -> leaderBoardEntry)
      bodies += (leaderBoardEntry.id -> player.getId)
      context.watch(s)
      scene.addPlayer(player)
      s ! Registered(leaderBoardEntry.id)
    case UpdateControls(velocity, angle) =>
//      TODO: handle shit when no uuid
      val s = sender()
      for {
        client <- clients.get(s)
        uuid <- bodies.get(client)
      } scene.updateControls(uuid, velocity, angle)
    case EmitBullet =>
      //      TODO: handle shit when no uuid
//      TODO TEST THIS
      val s = sender()
      val uuidOption: Option[UUID] =
        for {
          client <- clients get s
          body <- bodies get client
        } yield body
      uuidOption match {
        case Some(uuid) => scene.emitBullet(uuid)
        case None =>
          for {
            client <- clients get s
          } {
            val Point(x, y) = SpawnPool.defaultPool.randomSpawn
            val player = new Player(position = new Vector2(x, y))
            //          TODO this throws
            bodies += (client -> player.getId)
            scene.addPlayer(player)
          }
      }
//      TODO respawn
    case Step =>
      val murders = scene.step()
      processMurders(murders)
      val updates = scene.updates.marshall
//      TODO this is huevo, ideas:
//      1) inverted bodies collection
//      2) some extra serialization logix
      val pimpedUpdates: Updates = updates.copy(leaderBoard = leaderBoard.values.toSet, bodies = updates.bodies.map(body => {
        bodies.find(_._2 equals body.uuid).fold(body)((x: (UUID, UUID)) => body.copy(uuid = x._1))
      }))
//      clients.foreach { _._1 ! PublishUpdates(updates) }
      clients.foreach { _._1 ! PublishUpdates(pimpedUpdates) }
    case Terminated(client) =>
//      println(s"terminated ${clients(client)}")
//      TODO: remove body from world
      val clientOption = clients.get(client)
      clientOption.foreach(leaderBoard -= _)
      val bodyOption = clientOption.flatMap(bodies.get)
      bodyOption foreach { body =>
        bodies -= body
        scene removePlayerById body
      }
      clients -= client
    case _ =>
  }
}
