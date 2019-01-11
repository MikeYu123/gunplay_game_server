package com.mikeyu123.gunplay.objects.huy

import java.util.UUID

import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.{Geometry, MassType, Vector2}
import Player.{defaultHeight, defaultWidth}

/**
  * Created by mihailurcenkov on 13.07.17.
  */
object Player {
  val defaultWidth: Double = 10d
  val defaultHeight: Double = 10d

}
class Player(width: Double = defaultWidth,
             height: Double = defaultHeight,
             position: Vector2 = new Vector2(0, 0),
             velocity: Vector2 = new Vector2(0,0))
      extends Body() {
  addFixture(Geometry.createRectangle(width, height))
  setLinearVelocity(velocity)
  translate(position)
  setAngularVelocity(0.0)
  setMass(MassType.NORMAL)
}
