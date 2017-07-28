package com.mikeyu123.gunplay.objects

import com.mikeyu123.gunplay.TestUtils
import com.mikeyu123.gunplay_physics.objects.GraphicsObject
import com.mikeyu123.gunplay_physics.structs.{Point, Rectangle, Vector}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by mihailurcenkov on 26.07.17.
  */
class BodySpec extends FlatSpec with Matchers with TestUtils {

  def dummyBody: Body = {
    Body(dummyUuid,
      dummyGraphicsObject,
      Vector(0, 0),
      0)
  }

  it should "perform initBody correctly" in {
    Body.initBody(dummyUuid, 0, 0) should equal(dummyBody)
  }

  it should "perform step correctly" in {
    val expected =
      Body(dummyUuid,
      GraphicsObject(
        Rectangle(
          Point(-2, -3),
          Point(8, -3),
          Point(8, -1),
          Point(-2, -1)
        ),
        Point(3, -2)
      ),
      Vector(3, -2),
      0)

    val initialBody =
      Body(dummyUuid,
        dummyGraphicsObject,
        Vector(3, -2),
        0)

    initialBody.step should equal(expected)
  }
}
