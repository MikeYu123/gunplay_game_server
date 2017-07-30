package com.mikeyu123.gunplay.objects

import com.mikeyu123.gunplay_physics.objects.GraphicsObject
import com.mikeyu123.gunplay_physics.structs.{Point, Rectangle, Vector}

/**
  * Created by mihailurcenkov on 13.07.17.
  */
object Body {
  val defaultWidth: Double = 10d
  val defaultHeight: Double = 2d

//  init player with coordinates in given x and y
  def initBody(uuid: String, x: Double, y: Double): Body = {
    val rectangle = Rectangle(Point(x - defaultWidth / 2, y - defaultHeight / 2),
      Point(x + defaultWidth / 2, y - defaultHeight / 2),
      Point(x + defaultWidth / 2, y + defaultHeight / 2),
      Point(x - defaultWidth / 2, y + defaultHeight / 2)
    )
    Body(uuid, GraphicsObject(rectangle, Point(x, y)), Vector(0d, 0d), 0d)
  }

  def initBody(uuid: String, point: Point): Body = initBody(uuid, point.x, point.y)
}
case class Body(uuid: String, graphicsObject: GraphicsObject, velocity: Vector, angle: Double) {

  def this(uuid: String, rectangle: Rectangle, velocity: Vector, angle: Double) =
    this(uuid, GraphicsObject(rectangle, rectangle.center), velocity, angle)

  def step: Body =
    Body(uuid, graphicsObject.move(velocity), velocity, angle)
}
