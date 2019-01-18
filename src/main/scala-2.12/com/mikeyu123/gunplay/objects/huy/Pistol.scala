package com.mikeyu123.gunplay.objects.huy

import java.time.Instant
import java.time.temporal.ChronoUnit

import com.mikeyu123.gunplay.utils.Vector2


object Pistol {
  def apply(span: Long = 200, ammo: Double = Double.PositiveInfinity) = new Pistol(span, ammo)
}
class Pistol(span: Long = 200, var ammo: Double = Double.PositiveInfinity) extends Weapon {
  var lastFired = Instant.now

  def emit(player: Player): Set[Bullet] = {
    if(lastFired.plus(span, ChronoUnit.MILLIS).isBefore(Instant.now)) {
      val bullet = new Bullet(player.getId, position = player.getWorldCenter.add(Vector2(10,0).rotate(player.getTransform.getRotation)), velocity = Vector2(10, 0).rotate(player.getTransform.getRotation))
      bullet.getTransform.setRotation(player.getTransform.getRotation)
      bullet.setAsleep(false)
      lastFired = Instant.now
      Set(bullet)
    }
    else {
      Set()
    }
  }
}
