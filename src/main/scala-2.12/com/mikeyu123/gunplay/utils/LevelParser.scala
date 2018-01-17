package com.mikeyu123.gunplay.utils

import java.util.UUID

import com.mikeyu123.gunplay.server.messaging.UuidMarshalling
import com.mikeyu123.gunplay.utils.LevelParser.{DoorData, LevelData, WallData}
import com.mikeyu123.gunplay_physics.structs.Point
import com.typesafe.config.{Config, ConfigFactory}
import spray.json.DefaultJsonProtocol

//TODO: movethis
object LevelParser {
  case class WallData(width: Int, height: Int, x: Double, y: Double, uuid: UUID = UUID.randomUUID(), angle: Double = 0d)
  case class DoorData(width: Int, height: Int, x: Double, y: Double, uuid: UUID = UUID.randomUUID(), angle: Double = 0d)
  case class LevelData(doors: Set[DoorData], walls: Set[WallData])
}
trait LevelParser extends DefaultJsonProtocol with UuidMarshalling {
  implicit val pointFormat = jsonFormat2(Point)
  implicit val wallDataFormat = jsonFormat6(WallData)
  implicit val doorDataFormat = jsonFormat6(DoorData)
  implicit val levelDataFormat = jsonFormat2(LevelData)
}
