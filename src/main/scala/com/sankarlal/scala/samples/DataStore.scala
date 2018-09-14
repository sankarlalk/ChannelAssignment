package com.sankarlal.scala.samples

import java.util.UUID

import scala.collection.mutable.{MutableList => MList}

/**
  * Created by sankarlal on 9/6/17.
  */

case class Channel(id: UUID, name: String, var phoneNumber: Option[String] = None)
case class User(id: UUID, name: String)
case class Following(channelId: UUID, userId: UUID)
case class PhoneNumber(number: String)

object DataStore {
  val channels: MList[Channel] = MList.empty[Channel]
  val users: MList[User] = MList.empty[User]
  val followings: MList[Following] = MList.empty[Following]
  val phoneNumbers: MList[PhoneNumber] = MList.empty[PhoneNumber]
}
