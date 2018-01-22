package com.mikeyu123.gunplay.objects
import java.util.UUID

import com.mikeyu123.gunplay.objects.World.WorldUpdates
import com.mikeyu123.gunplay.server.{Updates}
import com.mikeyu123.gunplay.server.messaging.{ObjectsMarshaller}
import com.mikeyu123.gunplay.utils.GameContactListener
import com.mikeyu123.gunplay_physics.objects.{PhysicsObject, Scene}
import com.mikeyu123.gunplay_physics.structs.{ContactListener, Motion, PhysicsProperties, Point, QTree, Rectangle, SceneProperties, Vector}
import ObjectsMarshaller._
import com.mikeyu123.gunplay.utils.LevelParser.LevelData

/**
  * Created by mihailurcenkov on 13.07.17.
  */
object World{
  def apply(players: Set[Body] = Set(),
            bullets: Set[Bullet] = Set(),
            walls: Set[Wall] = Set(
              Wall(Rectangle(Point(60, 60), 200, 50), Point(60, 60), PhysicsProperties(Motion(Vector(0,0), 0))),
              Wall(Rectangle(Point(60, 60), 50, 200), Point(60, 60), PhysicsProperties(Motion(Vector(0,0), 0))),
            ),
//            walls: Set[Wall] = Set(),
            doors: Set[Door] = Set()) = {
    val scene = Scene(players ++ bullets ++ walls ++ doors, contactListener = GameContactListener)
    new World(scene)
  }

  def fromLevel(level: LevelData): World = {
    val walls = level.walls.map { wallData =>
      Wall(Rectangle(Point(wallData.x, wallData.y), wallData.width, wallData.height),
        Point(wallData.x, wallData.y),
        id = wallData.uuid)
    }
    val doors = level.doors.map { doorData =>
      Door(Rectangle(Point(doorData.x, doorData.y), doorData.width, doorData.height),
        Point(doorData.x, doorData.y),
        id = doorData.uuid)
    }
    World(Set[Body](), Set[Bullet](), walls, doors)
  }
//  TODO WorldUpdates & Updates Namings
  case class WorldUpdates(bodies: Set[Body] = Set(), bullets: Set[Bullet] = Set()) {
    def marshall : Updates = {
      Updates(
        bodies.map(_.marshall),
        bullets.map(_.marshall)
      )
    }
  }
}
case class World(scene: Scene) {
  def step : World = {
//    TODO: Add collision detection
//    TODO: Rework to actors
    World(scene.step)
  }

  def updates: WorldUpdates = {
    scene.objects.foldLeft(WorldUpdates())({ (acc: WorldUpdates, obj: PhysicsObject) => {
        obj match {
          case x: Body => WorldUpdates(acc.bodies + x, acc.bullets)
          case x: Bullet => WorldUpdates(acc.bodies, acc.bullets + x)
          case _ => acc
        }
      }
    })
  }


  def emitBullet(uuid: UUID): World = {
    scene.getObject(uuid).fold(this){
      case body: Body =>
        World(scene + body.emitBullet)
      case _ => this
    }
  }
//  TODO: rework with uuids
  def addPlayer(body: Body) : World = World(scene + body)

  def removePlayer(body: Body): World = World(scene - body)

  def removePlayerById(id: UUID): World = World(scene - id)

  def playerClick(body: Body) : World = {
//    TODO: add bullet
    World(scene)
  }

  def updateControls(id: UUID, velocity: Vector, angle: Double): World = {
    val oldPlayer = scene.getObject(id)
    val newPlayer = oldPlayer.map(p => Body(p.shape, p.center, PhysicsProperties(Motion(velocity, angle)), id))

    val sceneWithoutOldPlayer = oldPlayer.fold(scene)(scene - _)
    val newScene = newPlayer.fold(sceneWithoutOldPlayer)(sceneWithoutOldPlayer + _)
    World(newScene)
  }
}
