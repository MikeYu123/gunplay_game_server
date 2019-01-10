package com.mikeyu123.gunplay.utils

import java.security.SecureRandom

import com.mikeyu123.gunplay_physics.structs.Point

import scala.util.Random

/**
  * Created by mihailurcenkov on 30.07.17.
  */
object SpawnPool {
//  TODO make it configurable
  val defaultPoolSet: List[Point] = List(
    Point(-50, 50),
    Point(50, -50),
    Point(50, 50),
    Point(-50, -50),
    Point(250, 250),
    Point(-250, -250),
    Point(-250, 250),
    Point(250, -250),
    Point(650, 650),
    Point(-650, -250),
    Point(-650, 650),
    Point(650, -650)
  )

  val defaultPool = new SpawnPool(defaultPoolSet)
}

case class SpawnPool(poolSet: List[Point]) {
  val random = new Random(new SecureRandom())
  def randomSpawn: Point = random.shuffle(poolSet).head
}
