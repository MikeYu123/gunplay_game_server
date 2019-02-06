package com.mikeyu123.gunplay.server.api.levels

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.Message
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.mikeyu123.gunplay.db.models.{Level, Room}
import com.mikeyu123.gunplay.objects.Scene
import com.mikeyu123.gunplay.server.{ClientConnectionActor, ConnectionClose, RegisterConnection, WorldActor}
import com.mikeyu123.gunplay.server.WebServer.{clientMessagePoolSize, flow, levels, system}
import com.mikeyu123.gunplay.server.api.{ApiProtocol, RoutingObject}
import com.mikeyu123.gunplay.utils.LevelParser.LevelData
import com.redis.RedisClient
import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.push

import scala.util.{Failure, Success}

object Ws {
  case class RoomCreated(room: Room, status: String = "ok")
}
class Ws(actorSystem: ActorSystem, levelCollection: MongoCollection[Level], redisClient: RedisClient) extends RoutingObject with SprayJsonSupport with ApiProtocol {
  import actorSystem.dispatcher

  def worldActorFuture(worldId: String) = actorSystem.actorSelection(worldId).resolveOne
  //  TODO probably decouple clients and worldActor
  def client(worldActor: ActorRef) = actorSystem.actorOf(Props(classOf[ClientConnectionActor], worldActor))


  def flow(worldActor: ActorRef): Flow[Message, Message, Any] = {
    val c = client(worldActor)
    val in = Sink.actorRef(c, ConnectionClose)

    val out = Source.actorRef(clientMessagePoolSize, OverflowStrategy.fail).mapMaterializedValue { a =>
      c ! RegisterConnection(a)
      a
    }

    Flow.fromSinkAndSource(in, out)
  }

  val route = {
    get {
      cookie("sessid") { sessionId =>
        val flowFuture = for {
          roomId <- redisClient.hget[String]("session_$sessionId", "roomId")
          worldActor <- worldActorFuture(roomId.get)
        } yield flow(worldActor)
        onComplete(flowFuture) {
          case Success(flow) =>
            handleWebSocketMessages(flow)
        }
      }
    }
  }
}
