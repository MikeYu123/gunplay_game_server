package com.mikeyu123.gunplay.objects

import java.util.UUID

import com.mikeyu123.gunplay_physics.objects.{GraphicsObject, MovableObject, PhysicsObject}
import com.mikeyu123.gunplay_physics.structs.{GeometryPrimitive, Motion, PhysicsProperties, Point, Vector}

/**
  * Created by mihailurcenkov on 13.07.17.
  */
object Bullet {
  val width = 5
  val height = 1
}
case class Bullet(override val shape: GeometryPrimitive,
                  override val center: Point,
                  override val properties: PhysicsProperties,
                  override val id: UUID = UUID.randomUUID()) extends MovableObject(shape, center, properties, id) {
  override def move(vector: Vector): Bullet = {
    Bullet(shape.move(vector), center + vector, properties, id)
  }

  override def rotate(radians: Double): Bullet = {
    Bullet(shape.rotate(radians, center), center, properties, id)
  }

  override def applyMotion(motion: Motion): Bullet = {
    Bullet(shape.move(motion, center), center + motion.path, properties, id)
  }

  override def applyMotion: Bullet = {
    this.applyMotion(properties.motion)
  }

  override def setMotion(motion: Motion): Bullet = {
    Bullet(shape, center, properties.setMotion(motion), id)
  }
}