package com.aviansoft.user

class FriendUserModel {

    lateinit var uid : String
    lateinit var email : String
    lateinit var name : String


    constructor()

    constructor(uid: String, email: String, name: String) {
        this.uid = uid
        this.email = email
        this.name = name

    }
}