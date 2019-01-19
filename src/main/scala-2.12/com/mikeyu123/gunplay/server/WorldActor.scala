package com.mikeyu123.gunplay.server

import java.util.UUID

import akka.actor.{Actor, ActorRef, Terminated}
import com.mikeyu123.gunplay.objects._
import com.mikeyu123.gunplay.objects.huy.Scene.Murder
import com.mikeyu123.gunplay.objects.huy.{Player, Scene}
import com.mikeyu123.gunplay.server.ClientConnectionActor.{Leaderboard, Updates}
import com.mikeyu123.gunplay.server.WorldActor.LeaderboardEntry
import com.mikeyu123.gunplay.server.messaging.MessageObject
import com.mikeyu123.gunplay.utils.SpawnPool
import com.mikeyu123.gunplay_physics.structs.Point
import org.dyn4j.geometry.Vector2

/**
  * Created by mihailurcenkov on 25.07.17.
  */

object WorldActor {
  case class LeaderboardEntry(id: UUID = UUID.randomUUID, name: String = "", kills: Int = 0, deaths: Int = 0)
}

class WorldActor(val scene: Scene) extends Actor {
//  TODO: move to world constructor
  def this() = this(new Scene())
//  TODO: maybe mutable map?
  var clients: Map[ActorRef, UUID] = Map[ActorRef, UUID]()
  var bodies: Map[UUID, UUID] = Map()
  var leaderboard: Map[UUID, LeaderboardEntry] = Map()

  def processMurders(murders: Set[Murder]) = {
    murders.foreach(murder => {
      val killerId = bodies.find(_._2.equals(murder.killer)).map(_._1)
      val victimId = bodies.find(_._2.equals(murder.victim)).map(_._1)
      for {
        id <- killerId
        entry <- leaderboard.get(id)
      } leaderboard += (id -> entry.copy(kills = entry.kills + 1))
      for {
        id <- victimId
        entry <- leaderboard.get(id)
      } {
        leaderboard += (id -> entry.copy(deaths = entry.deaths + 1))
        bodies -= id
      }
    })
  }

  override def receive: Receive = {
    case AddPlayer(name) =>
      val s = sender()
      val player = scene.addPlayer
      val leaderBoardEntry = LeaderboardEntry(name = name)
      clients += (s -> leaderBoardEntry.id)
      leaderboard += (leaderBoardEntry.id -> leaderBoardEntry)
      bodies += (leaderBoardEntry.id -> player.getId)
      context.watch(s)
      s ! Registered(leaderBoardEntry.id)
    case UpdateControls(velocity, angle, click) =>
//      TODO: handle shit when no uuid
      val s = sender()
      val clientOption: Option[UUID] = clients.get(s)
      val uuidOption: Option[UUID] = clientOption.flatMap(bodies.get)
      uuidOption match {
        case Some(uuid) =>
          scene.updateControls(uuid, velocity, angle)
          if(click)
            scene.emitBullet(uuid)
        case None =>
          clientOption foreach { client =>
            val player = scene.addPlayer
            bodies += (client -> player.getId)
          }
      }
    case Step =>
      val murders = scene.step
      processMurders(murders)
      val updates: Updates = scene.updates.marshall
//      TODO this is huevo, ideas:
//      1) inverted bodies collection
//      2) some extra serialization logix
      val leaderboardData = leaderboard.values.toSeq.sortBy(-_.kills)
//      reset uuids
      val pimpedBodies = updates.bodies.map(
        body => {
          val bodyOption: Option[(UUID, UUID)] = bodies.find(_._2 equals body.uuid)
          bodyOption.fold(body)((x: (UUID, UUID)) => body.copy(uuid = x._1))
        }
      )
      val pimpedUpdates: Updates = updates.copy(bodies = pimpedBodies)

      val publishLeaderboard = leaderboardData.nonEmpty
      val leaderboardObject = Leaderboard(leaderboardData)
      clients.keys.foreach { c =>
        c ! pimpedUpdates
        if (publishLeaderboard)
          c ! leaderboardObject
      }
    case Terminated(client) =>
//      println(s"terminated ${clients(client)}")
//      TODO: remove body from world
      val clientOption = clients.get(client)
      clientOption.foreach(leaderboard -= _)
      val bodyOption = clientOption.flatMap(bodies.get)
      bodyOption foreach { body =>
        bodies -= body
        scene removePlayerById body
      }
      clients -= client
    case _ =>
  }
}
