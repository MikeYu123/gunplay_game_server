package com.mikeyu123.gunplay.server

import akka.actor.ActorSystem.Settings
import akka.actor.Terminated
import akka.dispatch.{PriorityGenerator, UnboundedStablePriorityMailbox}
import com.typesafe.config.Config

class WorldActorMailbox(settings: Settings, config: Config) extends UnboundedStablePriorityMailbox(PriorityGenerator {
  case UpdateControls(_,_,_) => 3
  case Step => 0
  case AddPlayer(_) => 1
  case Terminated(_) => 2
})
