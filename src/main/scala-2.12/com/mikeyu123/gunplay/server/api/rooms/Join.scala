package com.mikeyu123.gunplay.server.api.rooms

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.Redirection
import com.mikeyu123.gunplay.db.models.{Level, Room}
import com.mikeyu123.gunplay.objects.Scene
import com.mikeyu123.gunplay.server.WebServer.levels
import com.mikeyu123.gunplay.server.WorldActor
import com.mikeyu123.gunplay.server.api.{ApiProtocol, RoutingObject}
import com.mikeyu123.gunplay.server.api.rooms.Create.RoomCreated
import com.mongodb.client.model.Filters
import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.push
import spray.json._

import scala.util.{Failure, Success}


object Join {
  case class RoomCreated(room: Room, status: String = "ok")
}
class Join(userId: ObjectId, actorSystem: ActorSystem, roomCollection: MongoCollection[Room]) extends RoutingObject with SprayJsonSupport with ApiProtocol {
  import actorSystem.dispatcher

  val route = {
    get {
      path("room" / "join" / Segment) { roomId =>
        val levelFuture = for {
          room <- roomCollection.findOneAndUpdate(equal("_id", new ObjectId(roomId)), push("players", userId)).head
        } yield room.level
        onComplete(levelFuture) {
          case Success(levelId: ObjectId) =>
            redirect(s"/game?level=$levelId", StatusCodes.Found)
          case Failure(e) =>
            complete(e)
        }
      }
    }
  }
}
