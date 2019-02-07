package com.mikeyu123.gunplay.server.api

import com.mikeyu123.gunplay.db.models.{Level, Room, User}
import com.mikeyu123.gunplay.server.api.rooms.Create.{CreateRoom, RoomCreated}
import com.mikeyu123.gunplay.server.api.users.Login.{LoginForm, UserData}
import com.mikeyu123.gunplay.server.api.users.Register.RegisterForm
import com.mikeyu123.gunplay.utils.LevelParser
import org.mongodb.scala.bson.ObjectId
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat}

trait ApiProtocol extends DefaultJsonProtocol with LevelParser {
  implicit val objectIdJsonFormat = new JsonFormat[ObjectId] {
    override def write(obj: ObjectId) = JsString(obj.toString)

    override def read(json: JsValue) = new ObjectId(json.asInstanceOf[JsString].value)
  }
  implicit val levelJsonFormat = jsonFormat2(Level)
  implicit val roomJsonFormat = jsonFormat4(Room)
  implicit val roomCreatedJsonFormat = jsonFormat2(RoomCreated)
  implicit val userDataJsonFormat = jsonFormat3(UserData.apply)
  implicit val loginFormJsonFormat = jsonFormat2(LoginForm)
  implicit val registerFormJsonFormat = jsonFormat3(RegisterForm)
  implicit val createRoomFromat = jsonFormat2(CreateRoom)
}
