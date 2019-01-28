package com.mikeyu123.gunplay.weapons

import com.mikeyu123.gunplay.objects.{Bullet, Player}

trait Weapon {
  def emit(player: Player): Set[Bullet]
}
