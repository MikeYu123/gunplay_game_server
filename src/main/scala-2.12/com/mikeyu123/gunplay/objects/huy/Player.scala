package com.mikeyu123.gunplay.objects.huy

import java.util.UUID

import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.{Geometry, Vector2}
import Player.{defaultHeight, defaultWidth}

/**
  * Created by mihailurcenkov on 13.07.17.
  */
object Player {
  val defaultWidth: Double = 10d
  val defaultHeight: Double = 10d
  case class PlayerData(id: UUID = UUID.randomUUID)


}
class Player(width: Double = defaultWidth, height: Double = defaultHeight, position: Vector2 = new Vector2(0, 0), velocity: Vector2 = new Vector2(0,0)) {
  import Player.PlayerData
  val shape = new Body()
  shape.addFixture(Geometry.createRectangle(width, height))

  import org.dyn4j.geometry.MassType

  shape.setLinearVelocity(velocity)
  shape.translate(position)
  shape.setAngularVelocity(0.0)
  shape.setMass(MassType.NORMAL)
  shape.setUserData(PlayerData(shape.getId))

}
