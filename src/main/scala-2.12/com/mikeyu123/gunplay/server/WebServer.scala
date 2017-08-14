package com.mikeyu123.gunplay.server

/**
  * Created by mihailurcenkov on 19.07.17.
  */
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.ws.Message
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}

import scala.concurrent.duration._
import scala.io.StdIn

object WebServer extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val worldActor = system.actorOf(Props(classOf[WorldActor]))
//  TODO probably decouple clients and worldActor
  def client = system.actorOf(Props(classOf[ClientConnectionActor], worldActor))


  def flow: Flow[Message, Message, Any] = {
    val c = client
    val in = Sink.actorRef(c, ConnectionClose)

    val out = Source.actorRef(8, OverflowStrategy.fail).mapMaterializedValue { a =>
      c ! RegisterConnection(a)
      a
    }

    Flow.fromSinkAndSource(in, out)
  }

    import akka.http.scaladsl.Http
    val interface = "localhost"
    val port = 8090
    import akka.http.scaladsl.server.Directives._
    val source = Source.actorRef[ClientMessage](0, OverflowStrategy.fail)

    val route = get {
      akka.http.scaladsl.server.Directives.handleWebSocketMessages(flow)
    }

    val bindingFuture =
      Http().bindAndHandle(route, interface, port)

    import system.dispatcher

    val cancellable =
      system.scheduler.schedule(
        0 milliseconds,
        100 milliseconds,
        worldActor,
        Step)

    var line = StdIn.readLine()
    while(line != "end") {
      line = StdIn.readLine()
    }

//    bindingFuture
//      .flatMap(_.unbind()) // trigger unbinding from the port
//      .onComplete(_ => system.terminate()) // and shutdown when done
//    StdIn.readLine()


  }

