package com.example.chatapplication

class User {

    var name:String?=null
    var surname:String?=null
    var email:String?=null
    var uid:String?=null

    constructor()

    constructor(name:String?,surname:String?, email:String?, uid:String?)
    {
        this.name=name
        this.surname=surname
        this.email=email
        this.uid=uid
    }
}