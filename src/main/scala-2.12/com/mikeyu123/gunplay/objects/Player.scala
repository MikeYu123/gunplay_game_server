package com.mikeyu123.gunplay.objects

import com.mikeyu123.gunplay_physics.objects.GraphicsObject
import com.mikeyu123.gunplay_physics.structs.{Rectangle, Vector}

/**
  * Created by mihailurcenkov on 13.07.17.
  */
case class Player(graphicsObject: GraphicsObject, velocity: Vector, angle: Double) {
  def this(rectangle: Rectangle) = this(GraphicsObject(rectangle, rectangle.center))

  def step: Player = this
}
