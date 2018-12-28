package com.mikeyu123.gunplay.utils

import com.mikeyu123.gunplay.server.Controls
import com.mikeyu123.gunplay_physics.structs.Vector
import org.dyn4j.geometry.Vector2

/**
  * Created by mihailurcenkov on 30.07.17.
  */
object ControlsParser {
  val stepSize: Double = 2d
  def parseControls(controls: Controls): (Vector2, Double, Boolean) = {
//    TODO: why -1?
    val dy = (if (controls.up) -stepSize else 0) + (if(controls.down) stepSize else 0)
    val dx = (if (controls.right) stepSize else 0) + (if(controls.left) -stepSize else 0)
    (new Vector2(dx, dy), controls.angle, controls.click)
  }
}
