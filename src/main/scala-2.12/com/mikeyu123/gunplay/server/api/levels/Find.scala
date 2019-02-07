package com.mikeyu123.gunplay.server.api.levels

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import com.mikeyu123.gunplay.db.models.{Level, Room}
import com.mikeyu123.gunplay.server.api.{ApiProtocol, RoutingObject}
import com.mikeyu123.gunplay.utils.LevelParser.LevelData
import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.push

import scala.util.{Failure, Success}

object Find {
  case class RoomCreated(room: Room, status: String = "ok")
}
class Find(actorSystem: ActorSystem, levelCollection: MongoCollection[Level]) extends RoutingObject with SprayJsonSupport with ApiProtocol {
  import actorSystem.dispatcher

  val route = {
    get {
      path("levels" / Segment) { levelId =>
        val levelFuture = for {
          level <- levelCollection.find(equal("_id", new ObjectId(levelId))).first.head
        } yield level.data
        onComplete(levelFuture) {
          case Success(levelData: LevelData) =>
            complete(levelData)
          case Failure(e) =>
            complete(e)
        }
      }
    }
  }
}
