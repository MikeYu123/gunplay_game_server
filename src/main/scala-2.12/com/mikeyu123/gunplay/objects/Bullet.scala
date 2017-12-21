package com.mikeyu123.gunplay.objects

import java.util.UUID

import com.mikeyu123.gunplay_physics.objects.{GraphicsObject, MovableObject, PhysicsObject}
import com.mikeyu123.gunplay_physics.structs.{GeometryPrimitive, PhysicsProperties, Point, Vector}

/**
  * Created by mihailurcenkov on 13.07.17.
  */
case class Bullet(override val shape: GeometryPrimitive,
                  override val center: Point,
                  override val properties: PhysicsProperties,
                  override val id: UUID) extends MovableObject(shape, center, properties, id) {
  def step: Bullet =
    Bullet(shape.move(properties.motion, center), center, properties, id)
}