package com.mikeyu123.gunplay

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import org.mindrot.jbcrypt.BCrypt

import scala.util.Properties

package object utils {
    val AppConfig: Config =
      Properties
        .envOrNone("CONFIG_FILE")
        .fold(ConfigFactory.load())(filename => ConfigFactory.parseFile(new File(filename)))

    case class Bcrypt(salt: String = BCrypt.gensalt) {
      def encodePassword(password: String) = {
        BCrypt.hashpw(password, salt)
      }
    }
    val bcryptSalt = if (AppConfig.hasPath("bcrypt.salt"))
            Bcrypt(AppConfig.getString("bcrypt.salt"))
          else
            Bcrypt()

}
