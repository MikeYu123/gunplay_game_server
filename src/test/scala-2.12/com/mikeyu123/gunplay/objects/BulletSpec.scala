package com.mikeyu123.gunplay.objects

import com.mikeyu123.gunplay.TestUtils
import com.mikeyu123.gunplay_physics.objects.GraphicsObject
import com.mikeyu123.gunplay_physics.structs.{Point, Rectangle}
import com.mikeyu123.gunplay_physics.structs.Vector
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by mihailurcenkov on 26.07.17.
  */
//TODO refactor this partial init to TestUtils
class BulletSpec extends FlatSpec with Matchers with TestUtils {
  it should "invoke step correctly" in {
    val initial = Bullet(dummyUuid,
      GraphicsObject(
        Rectangle(
          Point(-5, -1),
          Point(5, -1),
          Point(5, 1),
          Point(-5, 1)
        ),
        Point(0, 0)
      ),
      Vector(1, -1)
    )

    val expected = Bullet(dummyUuid,
      GraphicsObject(
        Rectangle(
          Point(-4, -2),
          Point(6, -2),
          Point(6, 0),
          Point(-4, 0)
        ),
        Point(1, -1)
      ),
      Vector(1, -1)
    )

    initial.step should equal(expected)
  }
}
