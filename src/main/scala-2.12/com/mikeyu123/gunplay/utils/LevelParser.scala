package com.mikeyu123.gunplay.utils

import com.mikeyu123.gunplay.utils.LevelParser.{DoorData, LevelData, WallData}
import com.mikeyu123.gunplay_physics.structs.Point
import com.typesafe.config.{Config, ConfigFactory}
import spray.json.DefaultJsonProtocol

//TODO: movethis
object LevelParser {
  case class WallData(width: Int, height: Int, center: Point)
  case class DoorData(width: Int, height: Int, center: Point)
  case class LevelData(doors: Set[DoorData], walls: Set[WallData])
}
trait LevelParser extends DefaultJsonProtocol {
  implicit val pointFormat = jsonFormat2(Point)
  implicit val wallDataFormat = jsonFormat3(WallData)
  implicit val doorDataFormat = jsonFormat3(DoorData)
  implicit val levelDataFormat = jsonFormat2(LevelData)
}
