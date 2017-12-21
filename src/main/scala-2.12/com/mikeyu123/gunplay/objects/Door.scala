package com.mikeyu123.gunplay.objects

import java.util.UUID

import com.mikeyu123.gunplay_physics.objects._
import com.mikeyu123.gunplay_physics.structs.{GeometryPrimitive, PhysicsProperties, Point}

/**
  * Created by mihailurcenkov on 13.07.17.
  */
case class Door(override val shape: GeometryPrimitive,
                  override val center: Point,
                  override val properties: PhysicsProperties,
                  override val id: UUID) extends ImmovableObject(shape, center, properties, id) {
  def step: Door =
    Door(shape.move(properties.motion, center), center, properties, id)
}