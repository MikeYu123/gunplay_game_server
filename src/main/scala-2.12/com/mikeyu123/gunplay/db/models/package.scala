package com.mikeyu123.gunplay.db

import com.mikeyu123.gunplay.utils.LevelParser.{DoorData, LevelData, PinData, WallData}
import com.mikeyu123.gunplay.utils.Vector2
import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId

package object models {

  import org.mongodb.scala.bson.codecs.Macros._
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.bson.codecs.configuration.CodecRegistries.{fromRegistries, fromProviders}

  sealed trait MongoModel

  case class Level(data: LevelData, _id: ObjectId = ObjectId.get) extends MongoModel

  case class Room(capacity: Int, level: ObjectId, _id: ObjectId = ObjectId.get, players: Set[ObjectId] = Set()) extends MongoModel

  case class User(email: String, passwordHash: String, username: Option[String] = None, avatar: Option[String] = None, _id: ObjectId = ObjectId.get) extends MongoModel


}