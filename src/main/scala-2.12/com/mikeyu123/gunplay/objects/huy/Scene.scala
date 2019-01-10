package com.mikeyu123.gunplay.objects.huy

import java.util.UUID

import com.mikeyu123.gunplay.objects._
import com.mikeyu123.gunplay.objects.huy.Bullet.BulletData
import com.mikeyu123.gunplay.objects.huy.Door.{DoorData, Pin}
import com.mikeyu123.gunplay.objects.huy.Player.PlayerData
import com.mikeyu123.gunplay.objects.huy.Scene.WorldUpdates
import com.mikeyu123.gunplay.objects.huy.Wall.WallData
import com.mikeyu123.gunplay.server.Updates
import com.mikeyu123.gunplay_physics.objects.PhysicsObject
import com.mikeyu123.gunplay.server.messaging.ObjectsMarshaller.MarshallableBody
import com.mikeyu123.gunplay.utils.LevelParser.LevelData
import org.dyn4j.collision.manifold.Manifold
import org.dyn4j.collision.narrowphase.Penetration

import scala.collection.JavaConverters._
import org.dyn4j.dynamics._
import org.dyn4j.dynamics.contact._
import org.dyn4j.geometry.Vector2

import scala.collection.mutable

object Scene {
  case class WorldUpdates(bodies: Set[Body] = Set(), bullets: Set[Body] = Set(), doors: Set[Body] = Set()) {
    def marshall : Updates = {
      Updates(
        bodies.map(_.marshall),
        bullets.map(_.marshall),
        doors.map(_.marshall)
      )
    }
  }
  def fromLevel(level: LevelData): Scene = {
    val walls = level.walls.map { wallData =>
      new Wall(wallData.width, wallData.height, new Vector2(wallData.x, wallData.y), new Vector2(0,0))
    }
    val doors = level.doors.map { doorData =>
      new Door(doorData.width, doorData.height, new Vector2(doorData.x, doorData.y), new Vector2(0,0), Door.pin(doorData.pin))
    }
    val scene = new Scene()
    walls.foreach(wall => scene.world.addBody(wall.shape))
    doors.foreach(door => {
      scene.world.addBody(door.shape)
      scene.world.addBody(door.pinBody)
      scene.world.addJoint(door.joint)
    })
    scene
  }
}

class Scene() {
  val world = new World()
  world.setGravity(new Vector2(0,0))
  var bodiesToRemove = collection.mutable.Set[Body]()


  def handlePlayerDeath(player: Body, bullet: Body) = {
//    Check if body emitted by player
    if(!bullet.getUserData.asInstanceOf[BulletData].emitent.equals(player.getId))
      bodiesToRemove add player
  }

  def handleBulletDisposal(bullet: Body) = {
    bodiesToRemove add bullet
  }

  val listener = new CollisionListener {
    def internalCollisionHandler(body1: Body, body2: Body) = {
      (body1.getUserData, body2.getUserData) match {
        case (PlayerData(playerId), BulletData(_, bulletId)) =>
          handlePlayerDeath(body1, body2)
          false
        case (BulletData(_, bulletId), PlayerData(playerId)) =>
          handlePlayerDeath(body2, body1)
          false
        case (BulletData(_, bulletId), WallData(_)) =>
          handleBulletDisposal(body1)
          false
        case (BulletData(_, bulletId), DoorData(_)) =>
          handleBulletDisposal(body1)
          false
        case (DoorData(_), BulletData(_, bulletId)) =>
          handleBulletDisposal(body2)
          false
        case (WallData(_), BulletData(_, bulletId)) =>
          handleBulletDisposal(body2)
          false
        case (PlayerData(_), WallData(_)) =>
          true
        case (WallData(_), PlayerData(_)) =>
          true
        case (PlayerData(_), DoorData(_)) =>
          true
        case (DoorData(_), PlayerData(_)) =>
          true
        case (DoorData(_), WallData(_)) =>
//          true
          false
        case (WallData(_), DoorData(_)) =>
          false
//          true
        case x => false
      }
    }
    override def collision(body1: Body, fixture1: BodyFixture, body2: Body, fixture2: BodyFixture): Boolean =
      internalCollisionHandler(body1, body2)

    override def collision(body1: Body, fixture1: BodyFixture, body2: Body, fixture2: BodyFixture, penetration: Penetration): Boolean =
      internalCollisionHandler(body1, body2)

    override def collision(body1: Body, fixture1: BodyFixture, body2: Body, fixture2: BodyFixture, manifold: Manifold): Boolean =
      internalCollisionHandler(body1, body2)

    override def collision(contactConstraint: ContactConstraint): Boolean =
      internalCollisionHandler(contactConstraint.getBody1, contactConstraint.getBody2)
  }
  world.addListener(listener)

  def addPlayer(player: Player): Unit = {
    world.addBody(player.shape)
  }

  def emitBullet(uuid: UUID): Unit = {
    world.getBodies.asScala.find {
      body =>
        body.getId.equals(uuid)
    }.foreach(player => {
//      TODO: recalculate velocity via angle
      val bullet = new Bullet(uuid, position = player.getWorldCenter.add(new Vector2(10,0).rotate(player.getTransform.getRotation)), velocity = new Vector2(10, 0).rotate(player.getTransform.getRotation))
      bullet.shape.getTransform.setRotation(player.getTransform.getRotation)
      //  bullet.shape.translate(new Vector2(10,10).rotate(player.getTransform.getRotation))

      world.addBody(bullet.shape)
      bullet.shape.setAsleep(false)
    })
  }

  def updateControls(uuid: UUID, velocity: Vector2, angular: Double): Unit = {
    world.getBodies.asScala.find {
      body =>
        body.getId.equals(uuid)
    }.foreach(player => {
      player.setLinearVelocity(velocity)
//      player.setAngularVelocity(angular)
//      player.applyTorque(angular)
      player.getTransform.setRotation(angular)
      player.setAsleep(false)
    })
  }

  def step(): Unit = {
    world.step(150)
    bodiesToRemove.foreach(world.removeBody)
    bodiesToRemove.clear
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
        case x: PlayerData => WorldUpdates(acc.bodies + obj, acc.bullets, acc.doors)
        case x: BulletData => WorldUpdates(acc.bodies, acc.bullets + obj, acc.doors)
        case x: DoorData => WorldUpdates(acc.bodies, acc.bullets, acc.doors + obj)
        case _ => acc
      }
    })
  }
}
