package com.mikeyu123.gunplay.objects.huy

import java.util.UUID

import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.{Geometry, Vector2}
import Bullet.{defaultHeight, defaultWidth}
import org.dyn4j.geometry.MassType

object Bullet {
  val defaultWidth = 5
  val defaultHeight = 1
  case class BulletData(emitent: UUID, id: UUID = UUID.randomUUID)
}
class Bullet(emitent: UUID, width: Double = defaultWidth, height: Double = defaultHeight, position: Vector2 = new Vector2(0, 0), velocity: Vector2 = new Vector2(0, 0)) {
  import Bullet.BulletData
  val shape = new Body()
  shape.addFixture(Geometry.createRectangle(width, height))
  shape.setLinearVelocity(velocity)
  shape.translate(position)
  shape.setAngularVelocity(0.0)
  shape.setMass(MassType.FIXED_LINEAR_VELOCITY)
  shape.setUserData(BulletData(emitent, shape.getId))
}
