package com.mikeyu123.gunplay.objects


import java.util.UUID

import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.{Geometry, MassType}
import Player.{defaultHeight, defaultWidth}
import com.mikeyu123.gunplay.utils
import com.mikeyu123.gunplay.utils.Vector2
import com.mikeyu123.gunplay.weapons.Weapon

/**
  * Created by mihailurcenkov on 13.07.17.
  */
object Drop {
  val defaultWidth: Double = utils.AppConfig.getInt("drop.width")
  val defaultHeight: Double = utils.AppConfig.getInt("drop.height")

  def apply(weapon: Weapon,
            width: Double = defaultWidth,
            height: Double = defaultHeight,
            position: Vector2 = Vector2(0, 0),
            velocity: Vector2 = Vector2(0, 0)
           ) = new Drop(weapon, width, height, position, velocity)

}
class Drop(val weapon: Weapon,
           width: Double = defaultWidth,
           height: Double = defaultHeight,
           position: Vector2 = Vector2(0, 0),
           velocity: Vector2 = Vector2(0,0))
  extends Body() {

  addFixture(Geometry.createRectangle(width, height))
  setLinearVelocity(velocity)
  translate(position)
  setAngularVelocity(0.0)
  setMass(MassType.NORMAL)
}
