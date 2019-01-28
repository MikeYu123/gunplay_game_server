package com.mikeyu123.gunplay.objects.huy

import java.time.Instant
import java.time.temporal.ChronoUnit

import com.mikeyu123.gunplay.utils
import com.mikeyu123.gunplay.utils.Vector2



//TODO val
object Riffle {
  val bulletOffset = utils.AppConfig.getInt("bullet.offset")
  val span: Long = utils.AppConfig.getLong("riffle.span")
  val ammo = utils.AppConfig.getDouble("riffle.ammo")
  val bulletVelocity = utils.AppConfig.getDouble("riffle.velocity")
}
case class Riffle(span: Long = Riffle.span, bulletVelocity: Double = Riffle.bulletVelocity, var ammo: Double = Riffle.ammo) extends Weapon {
  var lastFired = Instant.now

  def emit(player: Player): Set[Bullet] = {
    if(lastFired.plus(span, ChronoUnit.MILLIS).isBefore(Instant.now) && ammo > 0) {
      val bullet = new Bullet(player.getId, position =
        player.getWorldCenter.add(
          Vector2(Riffle.bulletOffset, 0).rotate(player.getTransform.getRotation)),
        velocity = Vector2(bulletVelocity, 0).rotate(player.getTransform.getRotation)
      )
      bullet.getTransform.setRotation(player.getTransform.getRotation)
      bullet.setAsleep(false)
      lastFired = Instant.now
      ammo -= 1
      Set(bullet)
    }
    else {
      Set()
    }
  }
}
