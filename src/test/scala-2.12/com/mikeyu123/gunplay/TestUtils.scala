package com.mikeyu123.gunplay

import java.util.UUID

import com.mikeyu123.gunplay_physics.objects.GraphicsObject
import com.mikeyu123.gunplay_physics.structs.{Point, Rectangle}

/**
  * Created by mihailurcenkov on 27.07.17.
  */
trait TestUtils {
  val dummyUuid = UUID fromString "7e6b501a-59ea-43c3-b499-67cd82b421e2"

  val dummyGraphicsObject = GraphicsObject(
    Rectangle(
      Point(-5, -1),
      Point(5, -1),
      Point(5, 1),
      Point(-5, 1)
    ),
    Point(0, 0)
  )
}
