package com.mikeyu123.gunplay.objects

import com.mikeyu123.gunplay_physics.objects.GraphicsObject
import com.mikeyu123.gunplay_physics.structs.Vector

/**
  * Created by mihailurcenkov on 13.07.17.
  */
case class Bullet(uuid: String, graphicsObject: GraphicsObject, velocity: Vector, angle: Double) {
  def step =
    Bullet(uuid, graphicsObject.move(velocity), velocity, angle)
}