package com.mikeyu123.gunplay.utils

import com.mikeyu123.gunplay.server.Controls
import com.mikeyu123.gunplay_physics.structs.Vector

/**
  * Created by mihailurcenkov on 30.07.17.
  */
object ControlsParser {
  def parseControls(controls: Controls): (Vector, Double) = {
    val dx = (if (controls.up) 1 else 0) + (if(controls.down) -1 else 0)
    val dy = (if (controls.right) 1 else 0) + (if(controls.left) -1 else 0)
    (Vector(dx, dy), controls.angle)
  }
}
