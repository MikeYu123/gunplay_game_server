package com.mikeyu123.gunplay.server

/**
  * Created by mihailurcenkov on 19.07.17.
  */
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}

import scala.io.StdIn


case object ConnectionClose
case class RegisterConnection(connection: ActorRef)

object WebServer extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val client = system.actorOf(Props(classOf[ClientConnectionActor]))

  def flow: Flow[Message, Message, Any] = {

    val in = Sink.actorRef(client, ConnectionClose)

    val out = Source.actorRef(8, OverflowStrategy.fail).mapMaterializedValue { a =>
      client ! RegisterConnection(a)
      a
    }

    Flow.fromSinkAndSource(in, out)
  }

    import akka.http.scaladsl.Http
    import akka.http.scaladsl.model.ws.Message
    val interface = "localhost"
    val port = 8090
    import akka.http.scaladsl.server.Directives._
    val source = Source.actorRef[Message](0, OverflowStrategy.fail)

    val route = get {
      akka.http.scaladsl.server.Directives.handleWebSocketMessages(flow)
    }

    val bindingFuture =
      Http().bindAndHandle(route, interface, port)

//    println(s"Server online at http://localhost:8090/\nPress RETURN to stop...")
    var line = StdIn.readLine()
    while(line != "end") {
      client ! TextMessage.Strict(line)
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

