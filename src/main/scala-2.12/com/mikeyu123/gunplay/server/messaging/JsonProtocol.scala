package com.mikeyu123.gunplay.server.messaging

import com.mikeyu123.gunplay.server.WorldActor.LeaderBoardEntry
import com.mikeyu123.gunplay.server._
import spray.json.DefaultJsonProtocol

/**
  * Created by mihailurcenkov on 06.08.17.
  */
trait JsonProtocol extends DefaultJsonProtocol with UuidMarshalling {

  implicit val messageFormat = jsonFormat2(ClientMessage)
  implicit val controlsFormat = jsonFormat6(Controls)
  implicit val messageObjectFormat = jsonFormat6(MessageObject)
  implicit val leaderBoardEntryFormat = jsonFormat4(LeaderBoardEntry)
  implicit val updatesFormat = jsonFormat4(Updates)
  implicit val registerFormat = jsonFormat1(Register)
  implicit val registeredFormat = jsonFormat2(RegisteredMessage)
}
