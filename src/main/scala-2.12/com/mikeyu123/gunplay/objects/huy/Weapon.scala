package com.mikeyu123.gunplay.objects.huy

trait Weapon {
  def emit(player: Player): Set[Bullet]
}
