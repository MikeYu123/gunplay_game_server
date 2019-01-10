package com.mikeyu123.gunplay.server

import java.util.UUID

/**
  * Created by mihailurcenkov on 15.08.17.
  */
package object messaging {
  case class MessageObject(uuid: UUID, x: Double, y: Double, angle: Double, width: Double, height: Double)
}
