package com.mikeyu123.gunplay.server

import akka.actor.{Actor, ActorRef, Terminated}
import akka.http.scaladsl.model.ws.TextMessage

/**
  * Created by mihailurcenkov on 25.07.17.
  */

class ClientConnectionActor(worldActor: ActorRef, uuid: String) extends Actor {
//TODO possibly move connection to constructor to avoid Option handling
  var connection: Option[ActorRef] = None

  override def preStart(): Unit = {
    super.preStart()
    worldActor ! RegisterClient(uuid)
  }

  val receive: Receive = {
//    Connection initialized
    case RegisterConnection(a: ActorRef) =>
      connection = Some(a)
      context.watch(a)

//      Connection terminated
//      Todo necessary?
    case Terminated(a) if connection.contains(a) =>
      connection = None
      context.stop(self)

//      Sink close callback
    case ConnectionClose =>
      context.stop(self)

//    Here we take incoming messages
//      TODO: check whether there might be binary messages
    case TextMessage.Strict(t) =>
//controls update and registering here
//      connection.foreach(_ ! TextMessage.Strict(s"echo $t"))
    case _ => // ingore
  }

  override def postStop(): Unit = connection.foreach(context.stop)
}
