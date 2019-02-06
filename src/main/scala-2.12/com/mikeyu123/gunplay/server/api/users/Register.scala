package com.mikeyu123.gunplay.server.api.users

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{DateTime, StatusCodes}
import akka.http.scaladsl.model.headers.HttpCookie
import com.mikeyu123.gunplay.db.models.{Room, User}
import com.mikeyu123.gunplay.server.api.users.Login.{LoginForm, UserData}
import com.mikeyu123.gunplay.server.api.users.Register.RegisterForm
import com.mikeyu123.gunplay.server.api.{ApiProtocol, RoutingObject, Session, SessionData}
import com.mikeyu123.gunplay.utils.Bcrypt
import com.redis.RedisClient
import org.bson.types.ObjectId
import org.mindrot.jbcrypt.BCrypt
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters.{and, equal}
import org.mongodb.scala.model.Updates.push
import com.redis.serialization.DefaultFormats._

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Register {
  case class RegisterForm(email: String, password: String, passwordConfirmation: String)
  object UserData {
    def apply(user: User) =
      UserData(user.email, user.username, user.avatar)
  }
  case class UserData(email: String, username: Option[String] = None, avatar: Option[String] = None)
}
class Register(actorSystem: ActorSystem, userCollection: MongoCollection[User], redisClient: RedisClient)(implicit bcrypt: Bcrypt) extends RoutingObject with SprayJsonSupport with ApiProtocol {
  import actorSystem.dispatcher

  val route = {
    post {
      path("users" / "register") {
        entity(as[RegisterForm]) { form =>
          if (form.password.equals(form.passwordConfirmation)) {
            val passwordHash: String = bcrypt.encodePassword(form.password)
            val user = User(form.email, passwordHash)
            val userCreateFuture = userCollection.insertOne(user).head
            onComplete(userCreateFuture) {
              case Success(_) =>
                val session = Session(SessionData(user._id))
                val expires = DateTime.now + 1.hour.length
                setCookie(HttpCookie("sessid", session.id.toString, expires = Some(expires)))
                complete(redisClient.hmset(s"session_${session.id}", session.data.toMap).map((_) => UserData(user)))
            }
          }
          else
            complete("huy")
        }
      }
    }
  }
}
