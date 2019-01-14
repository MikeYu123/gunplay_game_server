package com.mikeyu123.gunplay.server

/**
  * Created by mihailurcenkov on 19.07.17.
  */
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.ws.Message
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import com.mikeyu123.gunplay.objects.huy.Scene
import com.mikeyu123.gunplay.utils.LevelParser
import com.mikeyu123.gunplay.utils.LevelParser.LevelData
import com.mikeyu123.gunplay_physics.objects.PhysicsObject
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.io.StdIn
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._


object WebServer extends App with LevelParser with SprayJsonSupport {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val levels = ConfigFactory.load("levels").as[List[LevelData]]("levels")
  val worldActor = system.actorOf(Props(classOf[WorldActor2], Scene.fromLevel(levels(0))))
//  TODO probably decouple clients and worldActor
  def client = system.actorOf(Props(classOf[ClientConnectionActor], worldActor))


  def flow: Flow[Message, Message, Any] = {
    val c = client
    val in = Sink.actorRef(c, ConnectionClose)

    val out = Source.actorRef(2000000, OverflowStrategy.fail).mapMaterializedValue { a =>
      c ! RegisterConnection(a)
      a
    }

    Flow.fromSinkAndSource(in, out)
  }

    import akka.http.scaladsl.Http
//    val interface = "localhost"
    val interface = "0.0.0.0"
    val port = 8090
    import akka.http.scaladsl.server.Directives._

    val route = get {
      akka.http.scaladsl.server.Directives.handleWebSocketMessages(flow)
    } ~
      path("levels" / IntNumber) { index =>
        get {
          respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")){
            complete(levels(0))
          }
        }
    }

    val bindingFuture =
      Http().bindAndHandle(route, interface, port)

    import system.dispatcher

    val cancellable =
      system.scheduler.schedule(
        0 milliseconds,
        50 milliseconds,
        worldActor,
        Step)
  }

