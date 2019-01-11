package com.mikeyu123.gunplay.objects.huy

import java.util.UUID

import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.{Geometry, MassType, Vector2}
import Wall.{defaultHeight, defaultWidth}

object Wall {
  val defaultWidth: Double = 100d
  val defaultHeight: Double = 20d
}
class Wall(width: Double = defaultWidth,
           height: Double = defaultHeight,
           position: Vector2 = new Vector2(0, 0),
           velocity: Vector2 = new Vector2(0, 0))
      extends Body() {
  addFixture(Geometry.createRectangle(width, height))
  translate(position)
  setLinearVelocity(velocity)
  setAngularVelocity(0.0)
  setMass(MassType.INFINITE)
}
