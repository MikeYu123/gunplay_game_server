package com.mikeyu123.gunplay.objects

import java.util.UUID

import com.mikeyu123.gunplay_physics.objects.{GraphicsObject, MovableObject, PhysicsObject}
import com.mikeyu123.gunplay_physics.structs.{Motion, PhysicsProperties, Point, Rectangle, Vector}

/**
  * Created by mihailurcenkov on 13.07.17.
  */
object Body {
  val defaultWidth: Double = 10d
  val defaultHeight: Double = 2d

//  init player with coordinates in given x and y
  def initBody(uuid: UUID, x: Double, y: Double): Body = {
    val rectangle = Rectangle(Point(x - defaultWidth / 2, y - defaultHeight / 2),
      Point(x + defaultWidth / 2, y - defaultHeight / 2),
      Point(x + defaultWidth / 2, y + defaultHeight / 2),
      Point(x - defaultWidth / 2, y + defaultHeight / 2)
    )
    Body(MovableObject(rectangle, Point(x, y), PhysicsProperties(Motion(Vector(0,0), 0)), uuid), Vector(0d, 0d), 0d)
  }

  def initBody(uuid: UUID, point: Point): Body = initBody(uuid, point.x, point.y)
}
case class Body(obj: PhysicsObject, velocity: Vector, angle: Double) {

  def this(uuid: String, rectangle: Rectangle, velocity: Vector, angle: Double) =
    this(uuid, GraphicsObject(rectangle, rectangle.center), velocity, angle)

  def step: Body =
    Body(obj.move(velocity), velocity, angle)
}
