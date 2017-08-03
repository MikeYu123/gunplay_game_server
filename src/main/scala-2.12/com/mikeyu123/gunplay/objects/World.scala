package com.mikeyu123.gunplay.objects
import com.mikeyu123.gunplay_physics.structs.Vector

/**
  * Created by mihailurcenkov on 13.07.17.
  */
case class World(players: Set[Body], bullets: Set[Bullet], walls: Set[Wall], doors: Set[Door]) {
  def step : World = {
//    TODO: Add collision detection
//    TODO: Rework to actors
    val newPlayers = players.map(_.step)
    val newBullets = bullets.map(_.step)
    val newDoors = doors.map(_.step)
    World(newPlayers, newBullets, walls, newDoors)
  }

  def addPlayer(body: Body) : World = World(players + body, bullets, walls, doors)

  def playerClick(body: Body) : World = {
//    TODO: add bullet
    World(players, bullets, walls, doors)
  }

  def updateControls(uuid: String, velocity: Vector, angle: Double): World = {
    val oldPlayer = players.find(_.uuid == uuid)
    val newPlayer = oldPlayer.map(p => Body(p.uuid, p.graphicsObject, velocity, angle))

    val playersWithoutOldPlayer = oldPlayer.fold(players)(players - _)
    val newPlayers = newPlayer.fold(playersWithoutOldPlayer)(playersWithoutOldPlayer + _)
    World(newPlayers, bullets, walls, doors)
  }
}
