package com.mikeyu123.gunplay.server.api.rooms

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.mikeyu123.gunplay.db.models.{Level, Room}
import com.mikeyu123.gunplay.objects.Scene
import com.mikeyu123.gunplay.server.WebServer.levels
import com.mikeyu123.gunplay.server.WorldActor
import com.mikeyu123.gunplay.server.api.{ApiProtocol, RoutingObject}
import com.mikeyu123.gunplay.server.api.rooms.Create.{RoomCreated, CreateRoom}
import com.mongodb.client.model.Filters
import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters._
import spray.json._


object Create {
  case class RoomCreated(room: Room, status: String = "ok")
  case class CreateRoom(capacity: Int, level: ObjectId)
}
class Create(actorSystem: ActorSystem, levelCollection: MongoCollection[Level], roomCollection: MongoCollection[Room]) extends RoutingObject with SprayJsonSupport with ApiProtocol {
  import actorSystem.dispatcher

  val route = {
    (post & path("room")) {
      cookie("sessId") { sessionId =>
        entity(as[CreateRoom]) { roomData =>
          complete(levelCollection.find(equal("_id", roomData.level)).first().head.flatMap { level =>
            val room = Room(roomData.capacity, roomData.level)
            actorSystem.actorOf(Props(classOf[WorldActor], Scene.fromLevel(level.data)).withMailbox("world-actor-mailbox"), room._id.toString)
            roomCollection.insertOne(room).head.map(_ => RoomCreated(room).toJson)
          })
        }
      }
    }
  }
}
