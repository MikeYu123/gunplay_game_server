package com.mikeyu123.gunplay.objects

import java.util.UUID

import com.mikeyu123.gunplay_physics.objects._
import com.mikeyu123.gunplay_physics.structs.{GeometryPrimitive, Motion, PhysicsProperties, Point, Vector}

/**
  * Created by mihailurcenkov on 13.07.17.
  */
case class Door(override val shape: GeometryPrimitive,
                  override val center: Point,
                  override val properties: PhysicsProperties,
                  override val id: UUID) extends ImmovableObject(shape, center, properties, id) {
  override def move(vector: Vector): Door = {
    Door(shape.move(vector), center + vector, properties, id)
  }

  override def rotate(radians: Double): Door = {
    Door(shape.rotate(radians, center), center, properties, id)
  }

  override def applyMotion(motion: Motion): Door = {
    Door(shape.move(motion, center), center + motion.path, properties, id)
  }

  override def applyMotion: Door = {
    this.applyMotion(properties.motion)
  }

  override def setMotion(motion: Motion): Door = {
    Door(shape, center, properties.setMotion(motion), id)
  }
}