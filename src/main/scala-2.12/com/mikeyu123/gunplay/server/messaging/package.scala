package com.mikeyu123.gunplay.server

import java.util.UUID

/**
  * Created by mihailurcenkov on 15.08.17.
  */
package object messaging {
  case class MessageObject(x: Double, y: Double, angle: Double, width: Double, height: Double)
  case class PlayerObject(x: Double, y: Double, angle: Double, width: Double, height: Double, weapon: String, ammo: Double)
}
