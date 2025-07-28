package com.example.doggo_ourapp

data class UserData(
    var name: String? = null,
    var surname: String? = null,
    var birthDate: String? = null,
    var bio: String? = null,
    var email: String? = null,
    var uid: String? = null,
    var points: String = "0",
    var photo:String? = null,
    var totalPoints: String = "0",
    var actualDog:String?=null,
    var administrator:String?="0"
) {

}
