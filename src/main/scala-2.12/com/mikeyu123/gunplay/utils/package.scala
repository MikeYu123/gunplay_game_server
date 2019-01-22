package com.mikeyu123.gunplay

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Properties

package object utils {
    val AppConfig: Config =
      Properties
        .envOrNone("CONFIG_FILE")
        .fold(ConfigFactory.load())(filename => ConfigFactory.parseFile(new File(filename)))
}
