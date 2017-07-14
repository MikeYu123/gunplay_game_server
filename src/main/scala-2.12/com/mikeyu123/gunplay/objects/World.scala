package com.mikeyu123.gunplay.objects

/**
  * Created by mihailurcenkov on 13.07.17.
  */
case class World(players: Set[Player], bullets: Set[Bullet], walls: Set[Wall], doors: Set[Door]) {
  def step : World = {
//    TODO: Add collision detection
//    TODO: Rework to actors
    val newPlayers = players.map(_.step)
    val newBullets = bullets.map(_.step)
    val newDoors = doors.map(_.step)
    World(newPlayers, newBullets, walls, newDoors)
  }
}
