package com.mikeyu123.gunplay.db
import com.mikeyu123.gunplay.db.models.{Level, MongoModel, Room, User}
import com.mikeyu123.gunplay.utils.LevelParser.{DoorData, LevelData, PinData, WallData}
import com.mikeyu123.gunplay.utils.Vector2
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._


import collection.JavaConverters._

object MongoManager {
  import org.mongodb.scala._


  // or provide custom MongoClientSettings
//  val settings: MongoClientSettings = MongoClientSettings.builder()
//    .applyToClusterSettings(b => b.hosts(List(new ServerAddress("localhost", 27017)).asJava))
//    .build()
  //  val mongoClient: MongoClient = MongoClient(settings)
  val codecRegistry =
    fromRegistries(
      fromProviders(classOf[Vector2], classOf[WallData], classOf[DoorData], classOf[PinData], classOf[LevelData], classOf[Level], classOf[Room], classOf[User]),
      DEFAULT_CODEC_REGISTRY )
    val mongoClient: MongoClient = MongoClient()

  val database: MongoDatabase = mongoClient.getDatabase("gunplay").withCodecRegistry(codecRegistry)

  implicit val usersCollection: MongoCollection[User] = database.getCollection("users")
  implicit val roomsCollection: MongoCollection[Room] = database.getCollection("rooms")
  implicit val levelsCollection: MongoCollection[Level] = database.getCollection("levels")

  def collection[T <: MongoModel](implicit e: MongoCollection[T]) : MongoCollection[T] = e

}
