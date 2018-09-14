package com.sankarlal.scala.samples

import java.util.UUID

import org.scalatest.FlatSpec

/**
  * Created by sankarlal on 9/6/17.
  */

class TesterDataStore extends FlatSpec {

  //Create Users
  val user1 = User(UUID.randomUUID(), "Sankar lal 1")
  val user2 = User(UUID.randomUUID(), "Sankar lal 2")
  val user3 = User(UUID.randomUUID(), "Sankar lal 3")
  DataStore.users += user1
  DataStore.users += user2
  DataStore.users += user3

  //Create PhoneNumbers
  DataStoreManager.addPhoneNumber(DataStore.phoneNumbers)
  DataStoreManager.addPhoneNumber(DataStore.phoneNumbers)

  //Create Channels
  val ch1 = Channel(UUID.randomUUID(), "CH-1", Some(DataStore.phoneNumbers.head.number))
  val ch2 = Channel(UUID.randomUUID(), "CH-2", Some(DataStore.phoneNumbers.head.number))
  val ch3 = Channel(UUID.randomUUID(), "CH-3", Some(DataStore.phoneNumbers.last.number))
  val ch4 = Channel(UUID.randomUUID(), "CH-4")
  DataStore.channels += ch1
  DataStore.channels += ch2
  DataStore.channels += ch3
  DataStore.channels += ch4

  override def withFixture(test: NoArgTest) = {
    // Shared setup (run at beginning of each test)
    try test()
    finally {
      // Shared cleanup (run at end of each test)
    }
  }

  behavior of "DataStoreManager"

  it should "return the size of the Following list as 1" in {
    val following1 = Following(ch1.id, user1.id)
    val returned = DataStoreManager.followChannel(following1, DataStore.channels, DataStore.followings, DataStore.phoneNumbers)
    assert(returned.size == 1)
  }

  it should "return the size of the Following list as 2" in {
    val following2 = Following(ch4.id, user1.id)
    val returned = DataStoreManager.followChannel(following2, DataStore.channels, DataStore.followings, DataStore.phoneNumbers)
    assert(returned.size == 2)
  }

  it should "return the size of the Following list as 3" in {
    val following3 = Following(ch2.id, user1.id)  //Now its collision
    val returned = DataStoreManager.followChannel(following3, DataStore.channels, DataStore.followings, DataStore.phoneNumbers)
    assert(returned.size == 3)
  }

}

