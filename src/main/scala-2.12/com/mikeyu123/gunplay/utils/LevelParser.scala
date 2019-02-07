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
  case class LevelData(doors: Seq[DoorData], walls: Seq[WallData], spawns: List[Vector2], dropSpawns: List[Vector2])
}
trait LevelParser extends DefaultJsonProtocol with UuidMarshalling {
  implicit val vector2Format = jsonFormat(Vector2, "x", "y")
  implicit val pointFormat = jsonFormat2(Point)
  implicit val wallDataFormat = jsonFormat5(WallData)
  implicit val pinDataFormat = jsonFormat5(PinData)
  implicit val doorDataFormat = jsonFormat6(DoorData)
  implicit val levelDataFormat = jsonFormat4(LevelData)
}
