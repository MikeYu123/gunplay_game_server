package com.mikeyu123.gunplay.server.messaging

import com.mikeyu123.gunplay.objects.{Drop, Player}
import com.mikeyu123.gunplay.weapons.{Pistol, Riffle, Shotgun}
import org.dyn4j.dynamics.Body
import org.dyn4j.geometry.{Geometry, Rectangle}

/**
  * Created by mihailurcenkov on 31.07.17.
  */
//TODO: decompose via inheritance
object ObjectsMarshaller {
  implicit class MarshallableBody(body: Body) {
    def toMessageObject: MessageObject  = {
      val transform = body.getTransform
      val shape = body.getFixture(0).getShape.asInstanceOf[Rectangle]
      MessageObject(transform.getTranslationX, transform.getTranslationY, transform.getRotation, shape.getWidth, shape.getHeight)
    }
  }
  implicit class MarshallablePlayer(player: Player) {
    def toPlayerObject: PlayerObject = {
      val transform = player.getTransform
      val shape = player.getFixture(0).getShape.asInstanceOf[Rectangle]
//      FIXME String-style polymorphism
      val (weapon, ammo) = player.weapon match {
        case Some(Pistol(_, _, ammo)) => ("pistol", ammo)
        case Some(Shotgun(_, _, ammo)) => ("shotgun", ammo)
        case Some(Riffle(_, _, ammo)) => ("riffle", ammo)
        case None => ("unarmed", 0d)
      }
      PlayerObject(transform.getTranslationX, transform.getTranslationY, transform.getRotation, shape.getWidth, shape.getHeight, weapon, ammo)
    }
  }
  implicit class MarchallableDrop(drop: Drop) {
    def toDropObject = {
      val transform = drop.getTransform
      val weapon = drop.weapon match {
        case _: Pistol => "pistol"
        case _: Shotgun => "shotgun"
        case _: Riffle => "riffle"
      }
      DropObject(transform.getTranslationX, transform.getTranslationY, weapon)
    }
  }
}