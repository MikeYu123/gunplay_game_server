package com.mikeyu123.gunplay.objects.huy

import java.util.UUID

import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.{Geometry, MassType}
import Player.{defaultHeight, defaultWidth}
import com.mikeyu123.gunplay.utils.Vector2

/**
  * Created by mihailurcenkov on 13.07.17.
  */
object Player {
  val defaultWidth: Double = 10d
  val defaultHeight: Double = 10d

  def apply(width: Double = defaultWidth,
            height: Double = defaultHeight,
            position: Vector2 = Vector2(0, 0),
            velocity: Vector2 = Vector2(0, 0)
           ) = new Player(width, height, position, velocity)

}
class Player(width: Double = defaultWidth,
             height: Double = defaultHeight,
             position: Vector2 = Vector2(0, 0),
             velocity: Vector2 = Vector2(0,0))
      extends Body() {
  var weapon: Option[Weapon] = None

  def emitBullets: Set[Bullet] = weapon.fold(Set[Bullet]())(_.emit(this))
  addFixture(Geometry.createRectangle(width, height))
  setLinearVelocity(velocity)
  translate(position)
  setAngularVelocity(0.0)
  setMass(MassType.NORMAL)
}
