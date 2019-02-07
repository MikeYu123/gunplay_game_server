package com.mikeyu123.gunplay.server

/**
  * Created by mihailurcenkov on 19.07.17.
  */
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.ws.Message
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.util.Timeout
import com.mikeyu123.gunplay.db.MongoManager
import com.mikeyu123.gunplay.objects.Scene
import com.mikeyu123.gunplay.server.api.levels.{Find, Ws}
import com.mikeyu123.gunplay.utils
import com.mikeyu123.gunplay.utils.LevelParser
import com.mikeyu123.gunplay.utils.LevelParser.LevelData
import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import com.mikeyu123.gunplay.db.MongoManager._
import com.mikeyu123.gunplay.db.models.Level
import com.mikeyu123.gunplay.server.api.rooms.{Create, Join}
import com.mikeyu123.gunplay.server.api.users.{Login, Register}
import com.redis.RedisClient

import scala.concurrent.duration._


object NewWebServer extends App with LevelParser with SprayJsonSupport {
  val interface = utils.AppConfig.getString("server.interface")
  val port = utils.AppConfig.getInt("server.port")
  val clientMessagePoolSize = utils.AppConfig.getInt("server.clientMessagePoolSize")
  implicit val system = ActorSystem()
  import system.dispatcher
  implicit val bcrypt = utils.bcryptSalt
  implicit val materializer = ActorMaterializer()
    import akka.http.scaladsl.Http
    import akka.http.scaladsl.server.Directives._

    val redisClient = RedisClient("localhost")
  implicit val timeout = Timeout(5 seconds)

    val route =           respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
      (new Ws(system, MongoManager.levelsCollection, redisClient) +
        new Find(system, MongoManager.levelsCollection) +
        new Create(system, MongoManager.levelsCollection, MongoManager.roomsCollection) +
        new Join(system, MongoManager.roomsCollection, redisClient) +
        new Login(system, MongoManager.usersCollection, redisClient) +
        new Register(system, MongoManager.usersCollection, redisClient)).route
    }


    val bindingFuture =
      Http().bindAndHandle(route, interface, port)

  }

