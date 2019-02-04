package com.mikeyu123.gunplay.db
import com.mikeyu123.gunplay.db.models.{Level, Room, User}

import collection.JavaConverters._

object MongoManager {
  import org.mongodb.scala._


  // or provide custom MongoClientSettings
//  val settings: MongoClientSettings = MongoClientSettings.builder()
//    .applyToClusterSettings(b => b.hosts(List(new ServerAddress("localhost", 27017)).asJava))
//    .build()
  //  val mongoClient: MongoClient = MongoClient(settings)
    val mongoClient: MongoClient = MongoClient()

  val database: MongoDatabase = mongoClient.getDatabase("gunplay")

  implicit val usersCollection: MongoCollection[User] = database.getCollection("users")
  implicit val roomsCollection: MongoCollection[Room] = database.getCollection("rooms")
  implicit val levelsCollection: MongoCollection[Level] = database.getCollection("levels")
}
