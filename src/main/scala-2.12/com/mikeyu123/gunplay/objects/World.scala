package com.mikeyu123.gunplay.objects
import com.mikeyu123.gunplay_physics.objects.Scene
import com.mikeyu123.gunplay_physics.structs.Vector

/**
  * Created by mihailurcenkov on 13.07.17.
  */
case class World(players: Set[Body], bullets: Set[Bullet], walls: Set[Wall], doors: Set[Door], scene: Scene) {
  def step : World = {
//    TODO: Add collision detection
//    TODO: Rework to actors
    val newPlayers = players.map(_.step)
    val newBullets = bullets.map(_.step)
    val newDoors = doors.map(_.step)
    World(newPlayers, newBullets, walls, newDoors, scene)
  }

//  TODO: rework with uuids
  def addPlayer(body: Body) : World = World(players + body, bullets, walls, doors, scene)

  def playerClick(body: Body) : World = {
//    TODO: add bullet
    World(players, bullets, walls, doors, scene)
  }

  def updateControls(uuid: String, velocity: Vector, angle: Double): World = {
    val oldPlayer = players.find(_.obj.id == uuid)
    val newPlayer = oldPlayer.map(p => Body(p.obj, velocity, angle))

    val playersWithoutOldPlayer = oldPlayer.fold(players)(players - _)
    val newPlayers = newPlayer.fold(playersWithoutOldPlayer)(playersWithoutOldPlayer + _)
    World(newPlayers, bullets, walls, doors, scene)
  }
}
