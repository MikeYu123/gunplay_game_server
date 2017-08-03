package com.mikeyu123.gunplay.server.messaging

import com.mikeyu123.gunplay.objects.{Body, Bullet}

/**
  * Created by mihailurcenkov on 31.07.17.
  */
//TODO: decompose via inheritance
object ObjectsMarshaller {
  def marshallBody(body: Body) = {
    MessageObject(body.uuid, body.graphicsObject.center.x, body.graphicsObject.center.y, body.angle)
  }

  def marshallBullet(bullet: Bullet) = {
    MessageObject(bullet.uuid, bullet.graphicsObject.center.x, bullet.graphicsObject.center.y, bullet.angle)
  }
}
