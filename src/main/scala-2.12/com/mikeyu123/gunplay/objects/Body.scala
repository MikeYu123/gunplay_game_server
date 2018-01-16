package com.mikeyu123.gunplay.objects

import java.util.UUID

import com.mikeyu123.gunplay_physics.objects.{GraphicsObject, MovableObject, PhysicsObject}
import com.mikeyu123.gunplay_physics.structs.{GeometryPrimitive, Motion, PhysicsProperties, Point, Rectangle, Vector}

/**
  * Created by mihailurcenkov on 13.07.17.
  */
object Body {
  val defaultWidth: Double = 10d
  val defaultHeight: Double = 10d

//  init player with coordinates in given x and y
  def initBody(id: UUID, x: Double, y: Double): Body = {
    val rectangle = Rectangle(Point(x - defaultWidth / 2, y - defaultHeight / 2),
      Point(x + defaultWidth / 2, y - defaultHeight / 2),
      Point(x + defaultWidth / 2, y + defaultHeight / 2),
      Point(x - defaultWidth / 2, y + defaultHeight / 2)
    )
    Body(rectangle, Point(x, y), PhysicsProperties(Motion(Vector(0,0), 0d)), id)
  }

  def initBody(id: UUID, point: Point): Body = initBody(id, point.x, point.y)
}
case class Body(override val shape: GeometryPrimitive,
                override val center: Point,
                override val properties: PhysicsProperties,
                override val id: UUID)
  extends MovableObject(shape, center, properties, id) {

//  def this(uuid: String, rectangle: Rectangle, velocity: Vector, angle: Double) =
//    this(uuid, GraphicsObject(rectangle, rectangle.center), velocity, angle)

  override def move(vector: Vector): Body = {
    Body(shape.move(vector), center + vector, properties, id)
  }

  override def rotate(radians: Double): Body = {
    Body(shape.rotate(radians, center), center, properties, id)
  }

  override def applyMotion(motion: Motion): Body = {
    Body(shape.move(motion, center), center + motion.path, properties, id)
  }

  override def applyMotion: Body = {
    this.applyMotion(properties.motion)
  }

  override def setMotion(motion: Motion): Body = {
    Body(shape, center, properties.setMotion(motion), id)
  }
}
