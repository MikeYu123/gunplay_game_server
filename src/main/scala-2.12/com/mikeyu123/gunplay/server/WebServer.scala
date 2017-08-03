package com.mikeyu123.gunplay.server

/**
  * Created by mihailurcenkov on 19.07.17.
  */
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.Message
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import scala.concurrent.duration._

import scala.io.StdIn


case object ConnectionClose
case class RegisterConnection(connection: ActorRef)

object WebServer extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val worldActor = system.actorOf(Props(classOf[WorldActor]))
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

//    import system.dispatcher // for the future transformations
//    bindingFuture
//      .flatMap(_.unbind()) // trigger unbinding from the port
//      .onComplete(_ => system.terminate()) // and shutdown when done
//    StdIn.readLine()

//    binding.flatMap(_.unbind()).onComplete(_ => actorSystem.shutdown())
//    println("Server is down...")

  }

