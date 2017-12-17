package com.mikeyu123.gunplay.objects

import com.mikeyu123.gunplay_physics.objects.{GraphicsObject, PhysicsObject}
import com.mikeyu123.gunplay_physics.structs.Vector

/**
  * Created by mihailurcenkov on 13.07.17.
  */
case class Bullet(obj: PhysicsObject, velocity: Vector, angle: Double) {
  def step =
    Bullet(obj.move(velocity), velocity, angle)
}