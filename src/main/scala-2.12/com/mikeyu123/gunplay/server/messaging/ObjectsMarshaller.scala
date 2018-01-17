package com.mikeyu123.gunplay.server.messaging

import com.mikeyu123.gunplay.objects.{Body, Bullet}
import com.mikeyu123.gunplay_physics.objects.PhysicsObject

/**
  * Created by mihailurcenkov on 31.07.17.
  */
//TODO: decompose via inheritance
object ObjectsMarshaller {
  implicit class MarshallablePhysicsObject(obj: PhysicsObject) {
    def marshall: MessageObject = {
      MessageObject(obj.id, obj.center.x, obj.center.y, obj.properties.motion.radians)
    }
  }
}
