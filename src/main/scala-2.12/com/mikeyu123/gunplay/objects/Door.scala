package com.mikeyu123.gunplay.objects

import java.util.UUID

import org.dyn4j.geometry.{Geometry, MassType}
import Door.{Pin, defaultHeight, defaultWidth, defaultDamping}
import com.mikeyu123.gunplay.utils
import com.mikeyu123.gunplay.utils.LevelParser.PinData
import com.mikeyu123.gunplay.utils.Vector2
import org.dyn4j.dynamics.Body
import org.dyn4j.dynamics.joint.RevoluteJoint

object Door {
  val defaultHeight = utils.AppConfig.getInt("door.height")
  val defaultWidth = utils.AppConfig.getInt("door.width")
  val defaultDamping = utils.AppConfig.getDouble("door.damping")
  case class Pin(x: Double, y: Double, width: Int, height: Int) extends Body() {
    addFixture(Geometry.createRectangle(width, height))
    setLinearVelocity(0,0)
    translate(x, y)
    setAngularVelocity(0)
    setMass(MassType.INFINITE)
    setMass(MassType.INFINITE)
  }
  def pin(pinData: PinData) = Pin(pinData.x, pinData.y, pinData.width, pinData.height)
}
class Door(width: Double = defaultWidth,
           height: Double = defaultHeight,
           position: Vector2 = Vector2(0, 0),
           velocity: Vector2 = Vector2(0, 0),
           damping: Double = defaultDamping,
           val pin: Pin) extends Body{
  addFixture(Geometry.createRectangle(width, height))
  setLinearVelocity(velocity)
  translate(position)
  setAngularVelocity(0.0)
  setAngularDamping(damping)
  setMass(MassType.NORMAL)
  val joint = new RevoluteJoint(this, pin, Vector2(pin.x, pin.y))
  joint.setLimitEnabled(true)
  joint.setLimits(-Math.PI / 2, Math.PI / 2)
}
