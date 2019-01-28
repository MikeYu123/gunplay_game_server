package com.mikeyu123.gunplay.server.messaging

import java.nio.ByteBuffer
import java.util.UUID

import akka.http.javadsl.model.ws.BinaryMessage
import akka.util.ByteString
import com.mikeyu123.gunplay.server.ClientConnectionActor._
import com.mikeyu123.gunplay.server.WorldActor.LeaderboardEntry

//TODO rewrite this to bytestring
trait BinaryProtocol {
  trait BinaryFormat[T] {
    def encode(t: T): ByteBuffer
    def decode(buffer: ByteBuffer) : T
  }
  implicit class BinaryEncode[T](t: T)(implicit ev: BinaryFormat[T]) {
    def toBinary : ByteBuffer = ev.encode(t)
  }
  implicit class BinaryDecode(byteBuffer: ByteBuffer) {
    def convertTo[T](implicit ev: BinaryFormat[T]) : T = ev.decode(byteBuffer)
  }

  implicit object RegisteredBinaryFormat extends BinaryFormat[Registered] {
    def encode(registered: Registered): ByteBuffer = {
      val id = registered.id
      val result = ByteBuffer.allocate(17).put(1.toByte).putLong(id.getMostSignificantBits).putLong(id.getLeastSignificantBits)
      result.flip()
      result
    }
    def decode(byteBuffer: ByteBuffer): Registered = {
      byteBuffer.get
      Registered(new UUID(byteBuffer.getLong, byteBuffer.getLong))
    }
  }

  implicit object MessageObjectBinaryFormat extends BinaryFormat[MessageObject] {
    def encode(messageObject: MessageObject): ByteBuffer = {
      val result = ByteBuffer.allocate(5 * 8)
        .putDouble(messageObject.x)
        .putDouble(messageObject.y)
        .putDouble(messageObject.angle)
        .putDouble(messageObject.width)
        .putDouble(messageObject.height)
      result.flip()
      result
    }

    def decode(byteBuffer: ByteBuffer): MessageObject = {
      MessageObject(byteBuffer.getDouble, byteBuffer.getDouble, byteBuffer.getDouble, byteBuffer.getDouble, byteBuffer.getDouble)
    }
  }

  implicit object PlayerObjectBinaryFormat extends BinaryFormat[PlayerObject] {
    def encodeWeaponType(weaponType: String): Byte = weaponType match {
      case "unarmed" => 0.toByte
      case "pistol" => 1.toByte
      case "shotgun" => 2.toByte
      case "riffle" => 3.toByte
    }

    def decodeWeaponType(byte: Byte): String = byte match {
      case 0 => "unarmed"
      case 1 => "pistol"
      case 2 => "shotgun"
      case 3 => "riffle"
    }

    def encode(playerObject: PlayerObject): ByteBuffer = {
      val result = ByteBuffer.allocate(6 * 8 + 1)
        .putDouble(playerObject.x)
        .putDouble(playerObject.y)
        .putDouble(playerObject.angle)
        .putDouble(playerObject.width)
        .putDouble(playerObject.height)
        .put(encodeWeaponType(playerObject.weapon))
        .putDouble(playerObject.ammo)
      result.flip()
      result
    }

    def decode(byteBuffer: ByteBuffer): PlayerObject = {
      PlayerObject(
        byteBuffer.getDouble,
        byteBuffer.getDouble,
        byteBuffer.getDouble,
        byteBuffer.getDouble,
        byteBuffer.getDouble,
        decodeWeaponType(byteBuffer.get),
        byteBuffer.getDouble
      )
    }
  }

  implicit object DropObjectBinaryFormat extends BinaryFormat[DropObject] {
    def encodeWeaponType(weaponType: String): Byte = weaponType match {
      case "pistol" => 1.toByte
      case "shotgun" => 2.toByte
      case "riffle" => 3.toByte
    }

    def decodeWeaponType(byte: Byte): String = byte match {
      case 1 => "pistol"
      case 2 => "shotgun"
      case 3 => "riffle"
    }

    def encode(playerObject: DropObject): ByteBuffer = {
      val result = ByteBuffer.allocate(2 * 8 + 1)
        .putDouble(playerObject.x)
        .putDouble(playerObject.y)
        .put(encodeWeaponType(playerObject.weapon))
      result.flip()
      result
    }

    def decode(byteBuffer: ByteBuffer): DropObject = {
      DropObject(
        byteBuffer.getDouble,
        byteBuffer.getDouble,
        decodeWeaponType(byteBuffer.get),
      )
    }
  }

  implicit object UpdatesBinaryFormat extends BinaryFormat[Updates] {
    def encode(updates: Updates): ByteBuffer = {
      val bodiesSize = updates.bodies.size
      val bulletsSize = updates.bullets.size
      val doorsSize = updates.doors.size
      val dropsSize = updates.doors.size
      val playerSize = updates.player.size
      val encode: MessageObject => ByteBuffer = MessageObjectBinaryFormat.encode
      val playerEncode: PlayerObject => ByteBuffer = PlayerObjectBinaryFormat.encode
      val dropEncode: DropObject => ByteBuffer = DropObjectBinaryFormat.encode

      val bodyMessages: ByteBuffer = updates.bodies.map(playerEncode).foldLeft(ByteBuffer.allocate(bodiesSize * 49))(_.put(_))
      bodyMessages.flip()
      val bulletMessages: ByteBuffer = updates.bullets.map(encode).foldLeft(ByteBuffer.allocate(bulletsSize * 40))(_.put(_))
      bulletMessages.flip()
      val doorMessages: ByteBuffer = updates.doors.map(encode).foldLeft(ByteBuffer.allocate(doorsSize * 40))(_.put(_))
      doorMessages.flip()
      val dropMessages: ByteBuffer = updates.drops.map(dropEncode).foldLeft(ByteBuffer.allocate(doorsSize * 17))(_.put(_))
      dropMessages.flip()
      val playerMessage: ByteBuffer = updates.player.map(playerEncode).getOrElse(ByteBuffer.allocate(0))
      val result = ByteBuffer.allocate(17 + bodiesSize * 49 + dropsSize * 17 + bulletsSize * 40 + doorsSize * 40 + playerSize * 49)
        .put(2.toByte)
        .putInt(bodiesSize)
        .put(bodyMessages)
        .putInt(bulletsSize)
        .put(bulletMessages)
        .putInt(doorsSize)
        .put(doorMessages)
        .putInt(dropsSize)
        .put(dropMessages)
        .putInt(playerSize)
        .put(playerMessage)
      result.flip()
      result
    }

    def decode(byteBuffer: ByteBuffer): Updates = {
      val decode: ByteBuffer => MessageObject = MessageObjectBinaryFormat.decode
      val playerDecode: ByteBuffer => PlayerObject = PlayerObjectBinaryFormat.decode
      val dropsDecode: ByteBuffer => DropObject = DropObjectBinaryFormat.decode
      byteBuffer.get
      val bodiesSize = byteBuffer.getInt
      val bodies = (1 to bodiesSize).map((_) => playerDecode(byteBuffer)).toSet
      val bulletsSize = byteBuffer.getInt
      val bullets = (1 to bulletsSize).map((_) => decode(byteBuffer)).toSet
      val doorsSize = byteBuffer.getInt
      val doors = (1 to doorsSize).map((_) => decode(byteBuffer)).toSet
      val dropsSize = byteBuffer.getInt
      val drops = (1 to dropsSize).map((_) => dropsDecode(byteBuffer)).toSet
      val playerSize = byteBuffer.getInt
      val player = (1 to playerSize).map(_ => playerDecode(byteBuffer)).headOption
      Updates(bodies, bullets, doors, drops, player)
    }
  }

  implicit object LeaderboardEntryBinaryFormat extends BinaryFormat[LeaderboardEntry] {
    def encode(leaderboardEntry: LeaderboardEntry): ByteBuffer = {
      val nameBytes = leaderboardEntry.name.getBytes("UTF-8")
      val size = 28 + nameBytes.length
      val result = ByteBuffer.allocate(size)
        .putLong(leaderboardEntry.id.getMostSignificantBits)
        .putLong(leaderboardEntry.id.getLeastSignificantBits)
        .putInt(nameBytes.length)
        .put(nameBytes)
        .putInt(leaderboardEntry.kills)
        .putInt(leaderboardEntry.deaths)
      result.flip()
      result
    }

    def decode(byteBuffer: ByteBuffer): LeaderboardEntry = {
      val id = new UUID(byteBuffer.getLong, byteBuffer.getLong)
      val nameLength = byteBuffer.getInt
      val nameBytes = (1 to nameLength).foldLeft(List[Byte]())((acc: List[Byte], _) => acc ::: (byteBuffer.get :: Nil)).toArray
      val name = new String(nameBytes, "UTF-8")
      LeaderboardEntry(id, name, byteBuffer.getInt, byteBuffer.getInt)
    }
  }


  implicit object LeaderboardBinaryFormat extends BinaryFormat[Leaderboard] {
    def encode(leaderboard: Leaderboard): ByteBuffer = {
      val encode: LeaderboardEntry => ByteBuffer = LeaderboardEntryBinaryFormat.encode
      val entries = leaderboard.entries
      val entriesCount = entries.size
      val encodedEntries: Seq[ByteBuffer] = entries.map(encode)
      val entriesSize = encodedEntries.map(_.limit).sum
      val bufferSize: Int = 5 + entriesSize
      val result = encodedEntries.foldLeft(ByteBuffer.allocate(bufferSize)
        .put(3.toByte)
        .putInt(entriesCount))(_.put(_))
      result.flip
      result
    }

    def decode(byteBuffer: ByteBuffer) = {
      val decode: ByteBuffer => LeaderboardEntry = LeaderboardEntryBinaryFormat.decode
      byteBuffer.get
      val count = byteBuffer.getInt
      val entries = (1 to count).map((_) => decode(byteBuffer))
      Leaderboard(entries)
    }
  }

  implicit object ServerMessageBinaryFormat extends BinaryFormat[ServerMessage] {

    def encode(serverMessage: ServerMessage): ByteBuffer = {
      val result = serverMessage match {
        case x: Updates => UpdatesBinaryFormat.encode(x)
        case x: Leaderboard => LeaderboardBinaryFormat.encode(x)
        case x: Registered => RegisteredBinaryFormat.encode(x)
      }
      result.flip()
      result
    }

    def decode(byteBuffer: ByteBuffer): ServerMessage = {
      byteBuffer.array.head match {
        case 1 => RegisteredBinaryFormat.decode(byteBuffer)
        case 2 => UpdatesBinaryFormat.decode(byteBuffer)
        case 3 => LeaderboardBinaryFormat.decode(byteBuffer)
      }
    }
  }

  implicit object RegisterBinaryFormat extends BinaryFormat[Register] {
    def encode(register: Register) = {
      val nameBytes: Array[Byte] = register.name.fold(Array[Byte]())(_.getBytes("UTF-8"))
      val nameLength = nameBytes.length
      val result = ByteBuffer.allocate(5 + nameLength)
          .put(1.toByte)
          .putInt(nameLength)
          .put(nameBytes)
      result.flip()
      result
    }

    def decode(byteBuffer: ByteBuffer) = {
      byteBuffer.get
      val nameLength = byteBuffer.getInt
      val nameBytes = (1 to nameLength).foldLeft(List[Byte]())((acc: List[Byte], _) => acc ::: (byteBuffer.get :: Nil)).toArray
      if (nameBytes.nonEmpty) {
        Register(Some(new String(nameBytes, "UTF-8")))
      }
      else{
        Register()
      }
    }
  }

  implicit object ControlsBinaryFormat extends BinaryFormat[Controls] {
    def encode(controls: Controls): ByteBuffer = {
      val controlsByte =
        ((if (controls.up) 1 else 0) +
        (if (controls.down) 2 else 0) +
        (if (controls.right) 4 else 0) +
        (if (controls.left) 8 else 0) +
        (if (controls.click) 16 else 0)).toByte
      val result = ByteBuffer.allocate(10)
          .put(2.toByte)
          .put(controlsByte)
          .putDouble(controls.angle)
      result.flip
      result
    }

    def decode(byteBuffer: ByteBuffer): Controls = {
      byteBuffer.get
      val controlsByte = byteBuffer.get
      val up = controlsByte % 2 == 1
      val down = (controlsByte / 2) % 2 == 1
      val right = (controlsByte / 4) % 2 == 1
      val left = (controlsByte / 8) % 2 == 1
      val click = (controlsByte / 16) % 2 == 1
      val angle = byteBuffer.getDouble
      Controls(up,down, left, right, angle, click)
    }
  }

  implicit object ClientMessageBinaryFormat extends BinaryFormat[ClientMessage] {

    def encode(clientMessage: ClientMessage): ByteBuffer = {
      val result = clientMessage match {
        case x: Controls => ControlsBinaryFormat.encode(x)
        case x: Register => RegisterBinaryFormat.encode(x)
      }
      result.flip()
      result
    }

    def decode(byteBuffer: ByteBuffer): ClientMessage = {
      val typeByte = byteBuffer.get()
      byteBuffer.position(0)
      typeByte match {
        case 2 => ControlsBinaryFormat.decode(byteBuffer)
        case 1 => RegisterBinaryFormat.decode(byteBuffer)
      }
    }
  }
}
