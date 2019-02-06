package com.mikeyu123.gunplay.server.api

class Login extends RoutingObject {
  val route = {
    (post & path("login")) {
      complete("huy")
    }
  }
}
