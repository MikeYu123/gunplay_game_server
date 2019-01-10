package com.mikeyu123.gunplay.server.messaging

import com.mikeyu123.gunplay.server.{ClientMessage, Controls, Updates}
import spray.json.DefaultJsonProtocol

/**
  * Created by mihailurcenkov on 06.08.17.
  */
trait JsonProtocol extends DefaultJsonProtocol with UuidMarshalling {

  implicit val messageFormat = jsonFormat2(ClientMessage)
  implicit val controlsFormat = jsonFormat6(Controls)
  implicit val messageObjectFormat = jsonFormat6(MessageObject)
  implicit val updatesFormat = jsonFormat3(Updates)
}
