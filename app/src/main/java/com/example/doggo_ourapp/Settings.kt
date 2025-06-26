package com.example.doggo_ourapp

import java.time.LocalDate

class Settings {

    var idUser:String?=null
    var notifications:Boolean?=null

    constructor()

    constructor(idUser:String?)
    {
        this.idUser
        this.notifications=false
    }

}
