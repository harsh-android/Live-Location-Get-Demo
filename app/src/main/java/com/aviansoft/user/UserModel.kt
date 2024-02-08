package com.aviansoft.user

class UserModel {

    lateinit var uid : String
    lateinit var email : String
    lateinit var name : String
    lateinit var location : LocLatLong

    constructor()

    constructor(uid: String, email: String, name: String, location: LocLatLong) {
        this.uid = uid
        this.email = email
        this.name = name
        this.location = location
    }
}