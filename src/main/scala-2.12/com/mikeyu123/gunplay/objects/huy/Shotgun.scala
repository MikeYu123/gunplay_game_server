package com.mikeyu123.gunplay.objects.huy

import java.time.Instant
import java.time.temporal.ChronoUnit

import com.mikeyu123.gunplay.utils
import com.mikeyu123.gunplay.utils.Vector2



//TODO bullets velocity override
object Shotgun {
  val bulletOffset = utils.AppConfig.getInt("bullet.offset")
  val span: Long = utils.AppConfig.getLong("shotgun.span")
  val ammo = utils.AppConfig.getDouble("shotgun.ammo")
  val bulletVelocity = utils.AppConfig.getDouble("shotgun.velocity")
}

case class Shotgun(span: Long = Shotgun.span, bulletVelocity: Double = Shotgun.bulletVelocity, var ammo: Double = Shotgun.ammo) extends Weapon {
  var lastFired = Instant.now

  def emit(player: Player): Set[Bullet] = {
    if(lastFired.plus(span, ChronoUnit.MILLIS).isBefore(Instant.now) && ammo > 0) {
      val bullets = Set(-Math.PI/6, -Math.PI / 3, 0d, Math.PI/6, Math.PI / 3).map(offset => {
        val bullet = new Bullet(player.getId, position =
          player.getWorldCenter.add(
            Vector2(Shotgun.bulletOffset, 0).rotate(player.getTransform.getRotation)),
          velocity = Vector2(bulletVelocity, 0).rotate(player.getTransform.getRotation + offset)
        )
        bullet.getTransform.setRotation(player.getTransform.getRotation + offset)
        bullet.setAsleep(false)
        bullet
      })
      lastFired = Instant.now
      ammo -= 1
      bullets
    }
    else {
      Set()
    }
  }
}
