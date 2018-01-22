package com.mikeyu123.gunplay.server.messaging

import com.mikeyu123.gunplay.server.{ClientMessage, Controls, Updates}
import spray.json.DefaultJsonProtocol

/**
  * Created by mihailurcenkov on 06.08.17.
  */
trait JsonProtocol extends DefaultJsonProtocol with UuidMarshalling {

  implicit val messageFormat = jsonFormat3(ClientMessage)
  implicit val controlsFormat = jsonFormat6(Controls)
  implicit val messageObjectFormat = jsonFormat4(MessageObject)
  implicit val updatesFormat = jsonFormat2(Updates)
}
