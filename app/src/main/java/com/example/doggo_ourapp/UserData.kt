package com.example.doggo_ourapp

import java.time.LocalDate

data class UserData(
    var name: String? = null,
    var surname: String? = null,
    var birthDate: LocalDate? = null,
    var bio: String? = null,
    var email: String? = null,
    var uid: String? = null,
    var points: String = "0",
    var settings: SettingsData? = null,
    var dogs: MutableList<DogData>? = mutableListOf(),
    var badgeAchieved: MutableList<BadgeAchievedData>? = mutableListOf(),
    var prizeAchieved: MutableList<PrizeAchievedData>? = mutableListOf()
) {

    init {
        if (settings == null) {
            settings = SettingsData(uid)
        }
    }
}
