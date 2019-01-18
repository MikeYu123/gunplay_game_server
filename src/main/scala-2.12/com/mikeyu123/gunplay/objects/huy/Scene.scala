package com.mikeyu123.gunplay.objects.huy

import java.util.UUID

import com.mikeyu123.gunplay.objects._
import com.mikeyu123.gunplay.objects.huy.Door.Pin
import com.mikeyu123.gunplay.objects.huy.Scene.{Murder, WorldUpdates}
import com.mikeyu123.gunplay.server.Updates
import com.mikeyu123.gunplay_physics.objects.PhysicsObject
import com.mikeyu123.gunplay.server.messaging.ObjectsMarshaller.MarshallableBody
import com.mikeyu123.gunplay.utils.LevelParser.LevelData
import com.mikeyu123.gunplay.utils.{SpawnPool, Vector2}
import org.dyn4j.collision.manifold.Manifold
import org.dyn4j.collision.narrowphase.Penetration

import scala.collection.JavaConverters._
import org.dyn4j.dynamics._
import org.dyn4j.dynamics.contact._

import scala.collection.mutable

object Scene {
  case class Murder(killer: UUID, victim: UUID)
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
      new Wall(wallData.width, wallData.height, Vector2(wallData.x, wallData.y), Vector2(0,0))
    }
    val doors = level.doors.map { doorData =>
      new Door(doorData.width, doorData.height, Vector2(doorData.x, doorData.y), Vector2(0,0), Door.pin(doorData.pin))
    }
    val scene = new Scene(SpawnPool(level.spawns))
    walls.foreach(wall => scene.world.addBody(wall))
    doors.foreach(door => {
      scene.world.addBody(door)
      scene.world.addBody(door.pin)
      scene.world.addJoint(door.joint)
    })
    scene
  }
}

class Scene(val spawnPool: SpawnPool = SpawnPool.defaultPool) {
  val world = new World()
  world.setGravity(Vector2(0,0))
  var bodiesToRemove = collection.mutable.Set[Body]()
  val murders = collection.mutable.Set[Murder]()


  def handlePlayerDeath(player: Body, bullet: Body) = {
//    Check if body emitted by player
    val emitentId = bullet.asInstanceOf[Bullet].emitent
    val playerId = player.getId
    if(!emitentId.equals(playerId)) {
      bodiesToRemove add player
      murders.add(Murder(emitentId, playerId))
    }
  }

  def handleBulletDisposal(bullet: Body) = {
    bodiesToRemove add bullet
  }

  val listener = new CollisionListener {
    def internalCollisionHandler(body1: Body, body2: Body) = {
      (body1, body2) match {
        case (_: Player, _: Bullet) =>
          handlePlayerDeath(body1, body2)
          false
        case (_: Bullet, _:Player) =>
          handlePlayerDeath(body2, body1)
          false
        case (_: Bullet, _: Wall) =>
          handleBulletDisposal(body1)
          false
        case (_: Bullet, _: Door) =>
          handleBulletDisposal(body1)
          false
        case (_: Wall, _: Bullet) =>
          handleBulletDisposal(body2)
        case (_: Pin, _: Bullet) =>
          handleBulletDisposal(body2)
          true
        case (_: Bullet, _: Pin) =>
          handleBulletDisposal(body1)
          true
        case (_: Door, _: Bullet) =>
          handleBulletDisposal(body2)
          false
        case (_: Player, _: Wall) =>
          true
        case (_: Wall, _: Player) =>
          true
        case (_: Player, _: Door) =>
          true
        case (_: Door, _: Player) =>
          true
        case (_: Door, _: Wall) =>
//          true
          false
        case (_: Wall, _: Door) =>
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

  def addPlayer: Player = {
    val player = Player(position = spawnPool.randomSpawn)
    player.weapon = Some(Pistol())
    world.addBody(player)
    player
  }

  def emitBullet(uuid: UUID): Unit = {
    world.getBodies.asScala.find {
      body =>
        body.getId.equals(uuid)
    }.foreach(player => {
//      TODO: recalculate velocity via angle
      for {
        bullet <- player.asInstanceOf[Player].emitBullets
      } world.addBody(bullet)
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

  def step: Set[Murder] = {
    world.step(150)
    bodiesToRemove.foreach(world.removeBody)
    bodiesToRemove.clear
    val returnSet: Set[Scene.Murder] = murders.toSet
    murders.clear()
    returnSet
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
      obj match {
        case x: Player => WorldUpdates(acc.bodies + obj, acc.bullets, acc.doors)
        case x: Bullet => WorldUpdates(acc.bodies, acc.bullets + obj, acc.doors)
        case x: Door => WorldUpdates(acc.bodies, acc.bullets, acc.doors + obj)
        case _ => acc
      }
    })
  }
}
