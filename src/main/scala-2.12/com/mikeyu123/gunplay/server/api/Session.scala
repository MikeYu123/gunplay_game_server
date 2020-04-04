package com.mikeyu123.gunplay.server.api

import java.util.UUID

import org.bson.types.ObjectId

object SessionData {
  def fromMap(map: Map[String, String]) = {
    SessionData(new ObjectId(map("userId")), map.get("roomId").map(new ObjectId(_)))
  }
}
case class SessionData(userId: ObjectId, roomId: Option[ObjectId] = None) {
  def toMap: Map[String, String] = {
    val userMap = Map("userId" -> userId.toString)
    roomId.fold(userMap)(room => userMap + ("roomId" -> room.toString))
  }
}
case class Session(data: SessionData, id: UUID = UUID.randomUUID)
