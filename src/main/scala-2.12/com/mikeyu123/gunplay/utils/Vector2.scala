package com.mikeyu123.gunplay.utils


case class Vector2(vecx: Double, vecy: Double) extends org.dyn4j.geometry.Vector2() {
  x = vecx
  y = vecy
}