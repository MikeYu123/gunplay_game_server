package com.mikeyu123.gunplay.utils

import java.security.SecureRandom

import scala.util.Random

/**
  * Created by mihailurcenkov on 30.07.17.
  */
object SpawnPool {
//  TODO make it configurable
  val defaultPoolSet: Set[Vector2] = Set(
    Vector2(-50, 50),
    Vector2(50, -50),
    Vector2(50, 50),
    Vector2(-50, -50),
    Vector2(250, 250),
    Vector2(-250, -250),
    Vector2(-250, 250),
    Vector2(250, -250),
    Vector2(650, 650),
    Vector2(-650, -250),
    Vector2(-650, 650),
    Vector2(650, -650)
  )

  val defaultPool = new SpawnPool(defaultPoolSet)
}

case class SpawnPool(poolSet: Set[Vector2], random: Random = new Random(new SecureRandom())) {
  def randomSpawn: Vector2 = random.shuffle(poolSet).head
}
