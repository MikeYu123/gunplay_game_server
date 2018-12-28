package com.mikeyu123.gunplay.objects.huy

import java.util.UUID

import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.{Geometry, Vector2}
import Wall.{defaultHeight, defaultWidth}

object Wall {
  val defaultWidth: Double = 100d
  val defaultHeight: Double = 20d
  case class WallData(id: UUID = UUID.randomUUID)
}
class Wall(width: Double = defaultWidth, height: Double = defaultHeight, position: Vector2 = new Vector2(0, 0), velocity: Vector2 = new Vector2(0, 0)) {
  import Wall.WallData
  val shape = new Body()
  shape.addFixture(Geometry.createRectangle(width, height))

  import org.dyn4j.geometry.MassType
  shape.translate(position)
  shape.setLinearVelocity(velocity)
  shape.setAngularVelocity(0.0)
  shape.setMass(MassType.INFINITE)
  shape.setUserData(WallData(shape.getId))
}
