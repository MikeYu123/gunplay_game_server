package com.mikeyu123.gunplay.objects

import com.mikeyu123.gunplay_physics.objects.GraphicsObject

/**
  * Created by mihailurcenkov on 13.07.17.
  */
case class Door(graphicsObject: GraphicsObject, currentAngle: Double, angularVelocity: Double) {
  def step = this
}
