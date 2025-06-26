package com.example.doggo_ourapp

import java.time.LocalDate

class User {

    var name:String?=null
    var surname:String?=null
    var birthDate:LocalDate?=null
    var bio:String?=null
    var email:String?=null

    var settings:Settings?=null

    var uid:String?=null

    constructor()

    constructor(name:String?,surname:String?,birthDate:LocalDate?,bio:String?, email:String?, uid:String?)
    {
        this.name=name
        this.surname=surname
        this.birthDate=birthDate
        this.bio=bio
        this.email=email
        this.uid=uid

        this.settings=Settings(uid)
    }
}
