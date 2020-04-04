package com.mikeyu123.gunplay.server.api

import akka.http.scaladsl.server.{Directives, Route}

abstract class RoutingObject extends Directives{
  val route: Route

//  Todo convert to monoid

  def +(routingObject: RoutingObject): RoutingObject = {
    val anotherRoute = routingObject.route
    val thisRoute = this.route
    new RoutingObject {
      override val route: Route = thisRoute ~ anotherRoute
    }
  }
}
