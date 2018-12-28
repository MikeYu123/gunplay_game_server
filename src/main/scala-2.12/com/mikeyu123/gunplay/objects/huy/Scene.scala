package com.mikeyu123.gunplay.objects.huy

import java.util.UUID

import com.mikeyu123.gunplay.objects._
import com.mikeyu123.gunplay.objects.huy.Bullet.BulletData
import com.mikeyu123.gunplay.objects.huy.Door.DoorData
import com.mikeyu123.gunplay.objects.huy.Player.PlayerData
import com.mikeyu123.gunplay.objects.huy.Scene.WorldUpdates
import com.mikeyu123.gunplay.objects.huy.Wall.WallData
import com.mikeyu123.gunplay.server.Updates
import com.mikeyu123.gunplay_physics.objects.PhysicsObject
import com.mikeyu123.gunplay.server.messaging.ObjectsMarshaller.MarshallableBody
import com.mikeyu123.gunplay.utils.LevelParser.LevelData

import scala.collection.JavaConverters._
import org.dyn4j.dynamics.{Body, World}
import org.dyn4j.dynamics.contact.{ContactListener, ContactPoint, PersistedContactPoint, SolvedContactPoint}
import org.dyn4j.geometry.Vector2

object Scene {
  case class WorldUpdates(bodies: Set[Body] = Set(), bullets: Set[Body] = Set()) {
    def marshall : Updates = {
      Updates(
        bodies.map(_.marshall),
        bullets.map(_.marshall)
      )
    }
  }
  def fromLevel(level: LevelData): Scene = {
    val walls = level.walls.map { wallData =>
      new Wall(wallData.width, wallData.height, new Vector2(wallData.x, wallData.y), new Vector2(0,0))
    }
    val doors = level.doors.map { doorData =>
      new Wall(doorData.width, doorData.height, new Vector2(doorData.x, doorData.y), new Vector2(0,0))
    }
    val scene = new Scene()
    walls.foreach(wall => scene.world.addBody(wall.shape))
    doors.foreach(door => scene.world.addBody(door.shape))
    scene
  }
}

class Scene() {
  val world = new World()
  world.setGravity(new Vector2(0,0))


  def handlePlayerDeath(playerId: UUID, bulletId: UUID) = {
    world.getBodies.asScala.filter(body => {
      val id = body.getId
      id.equals(playerId) || id.equals(bulletId)
    }).foreach(body => {
      world.removeBody(body)
    })
  }

  def handleBulletDisposal(bulletId: UUID) = {
    world.getBodies.asScala.find(body => {
      val id = body.getId
      id.equals(bulletId)
    }).foreach(body => {
      world.removeBody(body)
    })
  }
//
//  val listener = new ContactListener {
//    override def postSolve(point: SolvedContactPoint): Unit = {}

//    override def preSolve(point: ContactPoint): Boolean = {
//      (point.getBody1.getUserData, point.getBody2.getUserData) match {
//        case (PlayerData(playerId), BulletData(_, bulletId)) =>
//          handlePlayerDeath(playerId, bulletId)
//          false
//        case (BulletData(_, bulletId), PlayerData(playerId)) =>
//          handlePlayerDeath(playerId, bulletId)
//          false
//        case (BulletData(_, bulletId), WallData(_)) =>
//          handleBulletDisposal(bulletId)
//          false
//        case (BulletData(_, bulletId), DoorData(_)) =>
//          handleBulletDisposal(bulletId)
//          false
//        case (DoorData(_), BulletData(_, bulletId)) =>
//          handleBulletDisposal(bulletId)
//          false
//        case (WallData(_), BulletData(_, bulletId)) =>
//          handleBulletDisposal(bulletId)
//          false
//        case (PlayerData(_), WallData(_)) =>
//          true
//        case (WallData(_), PlayerData(_)) =>
//          true
//        case _ => false
//      }
//    }
//
//    override def sensed(point: ContactPoint): Unit = {}
//
//    override def end(point: ContactPoint): Unit = {}
//
//    override def persist(point: PersistedContactPoint): Boolean = true
//
//    override def begin(point: ContactPoint): Boolean = true
//  }
//  world.addListener(listener)

  def addPlayer(player: Player): Unit = {
    world.addBody(player.shape)
  }

  def emitBullet(uuid: UUID): Unit = {
    world.getBodies.asScala.find {
      body =>
        body.getId.equals(uuid)
    }.foreach(player => {
      val bullet = new Bullet(uuid, position = player.getWorldCenter, velocity = player.getLinearVelocity())
      bullet.shape.translate(player.getLinearVelocity())
      world.addBody(bullet.shape)
    })
  }

  def updateControls(uuid: UUID, velocity: Vector2, angular: Double): Unit = {
    world.getBodies.asScala.find {
      body =>
        body.getId.equals(uuid)
    }.foreach(player => {
      player.setLinearVelocity(velocity)
      player.setAngularVelocity(angular)
    })
  }

  def step(): Unit = {
    world.step(100)
  }

  def removePlayerById(uuid: UUID): Unit = {
    world.getBodies.asScala.find {
      body =>
        body.getId.equals(uuid)
    }.foreach(player => {
      world.removeBody(player)
    })
  }

  def updates: WorldUpdates = {
    world.getBodies.asScala.foldLeft(WorldUpdates())((acc: WorldUpdates, obj: Body) => {
      obj.getUserData match {
        case x: PlayerData => WorldUpdates(acc.bodies + obj, acc.bullets)
        case x: BulletData => WorldUpdates(acc.bodies, acc.bullets + obj)
        case _ => acc
      }
    })
  }
}
