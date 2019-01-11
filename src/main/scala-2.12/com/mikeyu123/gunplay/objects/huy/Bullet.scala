package com.mikeyu123.gunplay.objects.huy

import java.util.UUID

import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.{Geometry, Vector2}
import Bullet.{defaultHeight, defaultWidth}
import org.dyn4j.geometry.MassType

object Bullet {
  val defaultWidth = 5
  val defaultHeight = 1
}
class Bullet(val emitent: UUID,
             width: Double = defaultWidth,
             height: Double = defaultHeight,
             position: Vector2 = new Vector2(0, 0),
             velocity: Vector2 = new Vector2(0, 0)) extends Body() {
  addFixture(Geometry.createRectangle(width, height))
  setLinearVelocity(velocity)
  translate(position)
  setAngularVelocity(0.0)
  setMass(MassType.FIXED_LINEAR_VELOCITY)
}
