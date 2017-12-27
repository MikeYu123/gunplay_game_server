package com.mikeyu123.gunplay.utils

import com.mikeyu123.gunplay.objects.{Body, Bullet, Door, Wall}
import com.mikeyu123.gunplay_physics.objects.{ImmovableObject, MovableObject}
import com.mikeyu123.gunplay_physics.structs.{Contact, ContactListener, ContactState}

object GameContactListener extends ContactListener {
  override def preSolve(contact: Contact) = {
    contact.a -> contact.b match {
      case (x: Body, y: Bullet) =>
//        eliminateBody()
//        eliminateBullet()
      //      Contact(contact.ab, contact.normal, ContactState.RemoveBoth)
          Contact(contact.ab, contact.normal, ContactState.RemoveA)
      case (x: Bullet, y: Body) =>
        //        eliminateBody()
        //        eliminateBullet()
        //      Contact(contact.ab, contact.normal, ContactState.RemoveBoth)
      Contact(contact.ab, contact.normal, ContactState.RemoveB)
      case (x: Bullet, y: Wall) =>
//        eliminateBullet()
        Contact(contact.ab, contact.normal, ContactState.RemoveA)

      case (x: Bullet, y: Door) =>
//        eliminateBullet()
        Contact(contact.ab, contact.normal, ContactState.RemoveA)
      case (x: Door, y: Wall) =>
        contact
      case _ => contact
    }
  }

  override def postSolve(contact: Contact) = {
    contact
  }
}
