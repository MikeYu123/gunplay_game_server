package com.mikeyu123.gunplay.objects.huy

import java.util.UUID

import org.dyn4j.geometry.{Geometry, Vector2}
import Door.{DoorData, Pin, defaultHeight, defaultWidth}
import com.mikeyu123.gunplay.utils.LevelParser.PinData
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.joint.RevoluteJoint

object Door {
  val defaultHeight = 50d
  val defaultWidth = 50d
  case class DoorData(id: UUID = UUID.randomUUID)
  case class Pin(x: Double, y: Double, width: Int, height: Int)
  def pin(pinData: PinData) = Pin(pinData.x, pinData.y, pinData.width, pinData.height)
}
class Door(width: Double = defaultWidth, height: Double = defaultHeight, position: Vector2 = new Vector2(0, 0), velocity: Vector2 = new Vector2(0, 0),  pin: Pin) {
  val shape = new Body()
  val pinBody = new Body()
  pinBody.addFixture(Geometry.createRectangle(pin.width, pin.height))
  shape.addFixture(Geometry.createRectangle(width, height))

  import org.dyn4j.geometry.MassType

  shape.setLinearVelocity(velocity)
  pinBody.setLinearVelocity(0,0)
  pinBody.translate(pin.x, pin.y)
  shape.translate(position)
  shape.setAngularVelocity(0.0)
  shape.setAngularDamping(.2)
  pinBody.setAngularVelocity(0)
  shape.setMass(MassType.NORMAL)
  pinBody.setMass(MassType.INFINITE)
  val joint = new RevoluteJoint(shape, pinBody, new Vector2(pin.x, pin.y))
  joint.setLimitEnabled(true)
  joint.setLimits(-Math.PI / 2, Math.PI / 2)
  shape.setUserData(DoorData(shape.getId))
}
