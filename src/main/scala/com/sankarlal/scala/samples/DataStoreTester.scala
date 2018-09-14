package com.sankarlal.scala.samples

import java.util.UUID

/**
  * Created by sankarlal on 9/6/17.
  */
object DataStoreTester {
  def main(args: Array[String]): Unit = {
    println("DataStore Application is Started...")

    println("Create Users")
    val user1 = User(UUID.randomUUID(), "Sankar lal 1")
    val user2 = User(UUID.randomUUID(), "Sankar lal 2")
    val user3 = User(UUID.randomUUID(), "Sankar lal 3")
    DataStore.users += user1
    DataStore.users += user2
    DataStore.users += user3

    println("Create PhoneNumbers")
    DataStoreManager.addPhoneNumber(DataStore.phoneNumbers)
    DataStoreManager.addPhoneNumber(DataStore.phoneNumbers)

    println("Create Channels")
    val ch1 = Channel(UUID.randomUUID(), "CH-1", Some(DataStore.phoneNumbers.head.number))
    val ch2 = Channel(UUID.randomUUID(), "CH-2", Some(DataStore.phoneNumbers.head.number))
    val ch3 = Channel(UUID.randomUUID(), "CH-3", Some(DataStore.phoneNumbers.last.number))
    val ch4 = Channel(UUID.randomUUID(), "CH-4")
    DataStore.channels += ch1
    DataStore.channels += ch2
    DataStore.channels += ch3
    DataStore.channels += ch4

    println("Create following")
    val following1 = Following(ch1.id, user1.id)


    println("Follow the channel")
    //def followChannel(following: Following, channels: MList[Channel], followings: MList[Following], phoneNumbers: MList[PhoneNumber]): MList[Following] = {
    var returned = DataStoreManager.followChannel(following1, DataStore.channels, DataStore.followings, DataStore.phoneNumbers)
    println("returned following.size = " + returned.size)

    val following2 = Following(ch4.id, user1.id)
    returned = DataStoreManager.followChannel(following2, DataStore.channels, DataStore.followings, DataStore.phoneNumbers)
    println("returned following.size = " + returned.size)

    val following3 = Following(ch2.id, user1.id)  //Now its collision
    returned = DataStoreManager.followChannel(following3, DataStore.channels, DataStore.followings, DataStore.phoneNumbers)
    println("returned following.size = " + returned.size)  // Should be success if the phoneNumber is reAssigned
  }
}
