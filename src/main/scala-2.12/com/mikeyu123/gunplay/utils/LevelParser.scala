package com.mikeyu123.gunplay.utils

import java.util.UUID

import com.mikeyu123.gunplay.server.messaging.UuidMarshalling
import com.mikeyu123.gunplay.utils.LevelParser.{DoorData, LevelData, PinData, WallData}
import com.mikeyu123.gunplay_physics.structs.Point
import com.typesafe.config.{Config, ConfigFactory}
import spray.json.DefaultJsonProtocol

//TODO: movethis
object LevelParser {
  case class PinData(width: Int, height: Int, x: Double, y: Double, angle: Double = 0d)
  case class WallData(width: Int, height: Int, x: Double, y: Double, angle: Double = 0d)
  case class DoorData(width: Int, height: Int, x: Double, y: Double, pin: PinData, angle: Double = 0d)
  case class LevelData(doors: Set[DoorData], walls: Set[WallData])
}
trait LevelParser extends DefaultJsonProtocol with UuidMarshalling {
  implicit val pointFormat = jsonFormat2(Point)
  implicit val wallDataFormat = jsonFormat5(WallData)
  implicit val pinDataFormat = jsonFormat5(PinData)
  implicit val doorDataFormat = jsonFormat6(DoorData)
  implicit val levelDataFormat = jsonFormat2(LevelData)
}
