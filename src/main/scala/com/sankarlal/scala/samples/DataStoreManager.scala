package com.sankarlal.scala.samples



import scala.collection.mutable.{MutableList => MList}

/**
  * Created by sankarlal on 9/6/17.
  */
object DataStoreManager {

  /**
    * This fn will add the new PhoneNumbers by increasing the existing PhoneNumber by 1 - (This is just to avoid creating the PH manually)
    * @param phoneNumbers
    * @return
    */
  def addPhoneNumber(phoneNumbers: MList[PhoneNumber]): MList[PhoneNumber] = {
    println("addPhoneNumber() is Called")
    if (phoneNumbers.isEmpty) phoneNumbers += PhoneNumber("1000000000") else phoneNumbers += PhoneNumber((phoneNumbers.last.number.toInt + 1).toString)
  }

  /**
    * Find the collision channel by checking the already followed channel's PhoneNumbers
    * @param following - following
    * @param channels - mutable list of Channel obj
    * @param followings - mutable list of Following obj
    * @param newChannel - Channel obj which needs to be tested whether it has collision or not
    * @return - Returns Option[Channel] - If there is no collision the None otherwise collision channel
    */
  def getCollisionChannel(following: Following, channels: MList[Channel], followings: MList[Following], newChannel: Channel): Option[Channel] = {
    println("getCollisionChannel() is Called")
    val currentlyFollowing = followings.filter(x => x.userId.equals(following.userId))
    if (currentlyFollowing.isEmpty) return None    // If the new user is not following any other Channels then no Collision

    val channelsWithoutCurrentChannel = channels.filter(ch => !ch.id.equals(following.channelId))
    println("channelsWithoutCurrentChannel.size = " + channelsWithoutCurrentChannel.size )
    println("currentlyFollowing")
    currentlyFollowing.foreach(println)

    // Should not Follow the same Channel again
    //    if (currentlyFollowing.exists(fo => fo.channelId.equals(newChannel.id))) throw new IllegalArgumentException("User is following the same channel already - Hence can not be followed again")

    currentlyFollowing.foreach(localFollowing => {

      println("newChannel.phoneNumber = " + newChannel.phoneNumber)
      println("newChannel.Id = " + newChannel.id)
      channelsWithoutCurrentChannel.find(ch => ch.id.equals(localFollowing.channelId) && !ch.phoneNumber.isEmpty && ch.phoneNumber.get.equals(newChannel.phoneNumber.get)) match {
        case Some(oldChannel) => {
          println("following.channelId = " + following.channelId)
          println("oldChannel.Id = " + oldChannel.id)

          return Some(oldChannel)
        }
        case _ => println("getCollisionChannel case _")//Do Nothing
      }
    })
    None  // If there is not match in the above iteration then There is no Collision - Return None
  }

  /**
    * This will return less used channel out of two collision channels
    * @param oldChannel - old existing channel in the following list
    * @param newChannel - new channel which is getting followed
    * @return - Channel which has less impact
    */
  def getLeastAffectedChannel(oldChannel: Channel, newChannel: Channel): Channel = {
    println("getLeastAffectedChannel() is Called")
    //TODO: Here is the logic to get the least affected by using BroadCast statistics
    //But assumed and returning the newChanel as least affected as we dont have the info about broadcasting statistics
    newChannel
  }

  /**
    *
    * @param channel
    * @param phoneNumbers
    * @param channels
    * @param following
    * @param followings
    * @return
    */
  def reAssignPhoneNumber(channel: Channel, phoneNumbers: MList[PhoneNumber], channels: MList[Channel], following: Following, followings: MList[Following]): Channel = {
    println("reAssignPhoneNumber() is Called")
    //Filter the phone Numbers except the current one

    val channelsWithoutCurrentPh = channels.filter(ch => ch.phoneNumber.isDefined && !ch.phoneNumber.get.equals(channel.phoneNumber.get))
    channelsWithoutCurrentPh.foreach(println)
    val channelsSortedByPhCount = channelsWithoutCurrentPh.groupBy(ch => ch.phoneNumber.get).toSeq.sortWith((x, y) => x._2.size < y._2.size)
    channelsSortedByPhCount.foreach(println)
    val phoneNumbersWithoutCurrentPh = phoneNumbers.filter(ph => !ph.number.equals(channel.phoneNumber.get))
    phoneNumbersWithoutCurrentPh.foreach(println)

    val phonesTried = MList.empty[PhoneNumber]
    channelsSortedByPhCount.foreach{
      case(phoneNumber, channelList) => {
        channel.phoneNumber = Some(phoneNumber)   // Assign the new phone number
        phonesTried += PhoneNumber(phoneNumber)
        if (getCollisionChannel(following, channels, followings, channel).isEmpty) return channel  // Check if the phone Number is also causing the collision
      }
      case _ => throw new IllegalArgumentException("Channels sorted by PH is not in required format")
    }

    //None of the existing Phone Numbers which are used by other channels are not solving the collision problem - So need to try with unused phone Numbers
    val unUsedPh = phoneNumbersWithoutCurrentPh.diff(phonesTried)

    if (!unUsedPh.isEmpty) channel.phoneNumber = Some(unUsedPh.head.number)
    else channel.phoneNumber = Some(addPhoneNumber(phoneNumbers).last.number.toString)  //There are no unused Phone numbers - Add a new one
    channel

  }

  /**
    *
    * @param following
    * @param channels
    * @param followings
    * @param phoneNumbers
    * @return
    */
  def followChannel(following: Following, channels: MList[Channel], followings: MList[Following], phoneNumbers: MList[PhoneNumber]): MList[Following] = {
    println("followChannel() is Called")

    // Should not Follow the same Channel again
    if (followings.exists(fo => fo.channelId.equals(following.channelId) && fo.userId.equals(following.userId))) throw new IllegalArgumentException("User is following the same channel already - Hence can not be followed again")

    channels.find(x => x.id.equals(following.channelId)) match {
      case Some(newChannel) => {
        if (newChannel.phoneNumber.isEmpty) {
          followings += following
          return followings   // IF the phone number is empty then can be followed blindly as there wont be any collision
        }

        getCollisionChannel(following, channels, followings, newChannel) match {
          case None => {
            followings += following
            return followings   // No Collisiion. So this channel can be followed without any issue
          }
          case Some(oldChannel) => {
            println("followChannel - case some")
            val leastAffectedChannel = getLeastAffectedChannel(oldChannel, newChannel)
            reAssignPhoneNumber(leastAffectedChannel, phoneNumbers, channels, following, followings)
            followings += following
            return followings
          }
        }
      }
      case None => throw new IllegalArgumentException("Channel which are not existing can not be followed...!")
    }

  }




}