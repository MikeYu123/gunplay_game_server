package com.mikeyu123.gunplay.objects.huy

import java.util.UUID

import org.dyn4j.geometry.{Geometry, Vector2}
import Door.{DoorData, defaultHeight, defaultWidth}
import org.dyn4j.dynamics.Body

object Door {
  val defaultHeight = 50d
  val defaultWidth = 50d
  case class DoorData(id: UUID = UUID.randomUUID)
}
class Door(width: Double = defaultWidth, height: Double = defaultHeight, position: Vector2 = new Vector2(0, 0), velocity: Vector2 = new Vector2(0, 0)) {
  val shape = new Body()
  shape.addFixture(Geometry.createRectangle(width, height))

  import org.dyn4j.geometry.MassType

  shape.setLinearVelocity(velocity)
  shape.translate(position)
  shape.setAngularVelocity(0.0)
  shape.setMass(MassType.NORMAL)
  shape.setUserData(DoorData(shape.getId))
}
