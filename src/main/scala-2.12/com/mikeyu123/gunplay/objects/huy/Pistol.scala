package com.mikeyu123.gunplay.objects.huy

import java.time.Instant
import java.time.temporal.ChronoUnit

import com.mikeyu123.gunplay.utils
import com.mikeyu123.gunplay.utils.Vector2



//TODO val
object Pistol {
  val bulletOffset = utils.AppConfig.getInt("bullet.offset")
  val span: Long = utils.AppConfig.getLong("pistol.span")
  val ammo = utils.AppConfig.getDouble("pistol.ammo")
  def apply(span: Long = span, ammo: Double = ammo) = new Pistol(span, ammo)
}
class Pistol(span: Long = Pistol.span, var ammo: Double = Pistol.ammo) extends Weapon {
  var lastFired = Instant.now

  def emit(player: Player): Set[Bullet] = {
    if(lastFired.plus(span, ChronoUnit.MILLIS).isBefore(Instant.now) && ammo > 0) {
      val bullet = new Bullet(player.getId, position =
        player.getWorldCenter.add(
          Vector2(Pistol.bulletOffset, 0).rotate(player.getTransform.getRotation)),
          velocity = Vector2(Pistol.bulletOffset, 0).rotate(player.getTransform.getRotation)
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
