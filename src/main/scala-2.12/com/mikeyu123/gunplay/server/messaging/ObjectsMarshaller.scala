package com.mikeyu123.gunplay.server.messaging

import com.mikeyu123.gunplay_physics.objects.PhysicsObject
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.{Geometry, Rectangle}

/**
  * Created by mihailurcenkov on 31.07.17.
  */
//TODO: decompose via inheritance
object ObjectsMarshaller {
  implicit class MarshallableBody(body: Body) {
    def marshall: MessageObject  = {
      val transform = body.getTransform
      val shape = body.getFixture(0).getShape.asInstanceOf[Rectangle]
      MessageObject(body.getId, transform.getTranslationX, transform.getTranslationY, transform.getRotation, shape.getWidth, shape.getHeight)
    }
  }
}