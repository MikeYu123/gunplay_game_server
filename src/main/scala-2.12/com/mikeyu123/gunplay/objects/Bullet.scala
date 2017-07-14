package com.mikeyu123.gunplay.objects

import com.mikeyu123.gunplay_physics.objects.GraphicsObject
import com.mikeyu123.gunplay_physics.structs.Vector

/**
  * Created by mihailurcenkov on 13.07.17.
  */
case class Bullet(graphicsObject: GraphicsObject, velocity: Vector) {
  def step = Bullet(graphicsObject.move(velocity), velocity)
}
