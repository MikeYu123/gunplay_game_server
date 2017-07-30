package com.mikeyu123.gunplay.utils

import com.mikeyu123.gunplay_physics.structs.Point

import scala.util.Random

/**
  * Created by mihailurcenkov on 30.07.17.
  */
object SpawnPool {
  val pool: Set[Point] = Set(
    Point(-50, -50),
    Point(50, -50),
    Point(50, 50),
    Point(-50, -50)
  )

  def randomSpawn: Point = Random.shuffle(pool).head
}
