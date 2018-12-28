package com.mikeyu123.gunplay.utils

import com.mikeyu123.gunplay_physics.structs.Point

import scala.util.Random

/**
  * Created by mihailurcenkov on 30.07.17.
  */
object SpawnPool {
//  TODO make it configurable
  val defaultPoolSet: Set[Point] = Set(
    Point(-50, 50),
    Point(50, -50),
    Point(50, 50),
    Point(-50, -50)
  )

  val defaultPool = new SpawnPool(defaultPoolSet)
}

case class SpawnPool(poolSet: Set[Point]) {
  def randomSpawn: Point = Random.shuffle(poolSet).head
}
