package com.example.doggo_ourapp

import com.example.doggo_ourapp.DogFirebase.loadAllDog
import java.time.LocalDate

object BadgeFirebase {

    fun checkAndAssignBadgesByType(
        type: String,
        value: String,
        onComplete: (List<String>) -> Unit
    ) {
            loadAllBadge { allBadges ->
            if (allBadges == null) {
                onComplete(emptyList())
                return@loadAllBadge
            }

            val filteredBadges = allBadges.filter { it.type == type }

            getUserBadges(UserFirebase.getCurrentUserId()) { userBadges ->
                val alreadyAchievedIds = userBadges?.mapNotNull { it.idBadge } ?: emptyList()
                val newUnlockedBadges = mutableListOf<String>()

                val tasks = mutableListOf<com.google.android.gms.tasks.Task<Void>>()

                for (badge in filteredBadges) {
                    val thresholdInt = badge.threshold?.toIntOrNull()
                    val badgeId = badge.id

                    if (badgeId != null && thresholdInt != null &&
                        !alreadyAchievedIds.contains(badgeId) &&
                        value.toInt() >= thresholdInt
                    ) {
                        newUnlockedBadges.add(badge.name!!)

                        val task = FirebaseDB.getMDbRef()
                            .child("user").child(UserFirebase.getCurrentUserId()).child("badgeAchieved")
                            .push()
                            .setValue(
                                BadgeAchievedData(
                                    badgeId,
                                    LocalDate.now().toString()
                                )
                            )
                        tasks.add(task)
                    }
                }

                if (tasks.isEmpty()) {
                    onComplete(emptyList())
                } else {
                    com.google.android.gms.tasks.Tasks.whenAllComplete(tasks)
                        .addOnCompleteListener {
                            onComplete(newUnlockedBadges)
                        }
                }
            }
        }
    }

    /*
    fun addBadgeToUser(badgeId: String, onComplete: (Boolean) -> Unit) {

        val userRef = FirebaseDB.getMDbRef().child("user").child(UserFirebase.getCurrentUserId())

        userRef.get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(UserData::class.java)
            if (user != null) {
                val badges = user.badgeAchieved ?: mutableListOf()

                // Controlla se la badge è già presente
                val alreadyAchieved = badges.any { it.idBadge == badgeId }
                if (!alreadyAchieved) {
                    badges.add(BadgeAchievedData(badgeId, LocalDate.now()))
                    userRef.child("badgeAchieved").setValue(badges).addOnCompleteListener {
                        onComplete(it.isSuccessful)
                    }
                } else {
                    onComplete(false) // già presente
                }
            } else {
                onComplete(false)
            }
        }.addOnFailureListener {
            it.printStackTrace()
            onComplete(false)
        }
    }
    */
    fun getUserBadges(userId: String, onResult: (List<BadgeAchievedData>?) -> Unit) {
        val userRef = FirebaseDB.getMDbRef().child("user").child(userId).child("badgeAchieved")

        userRef.get().addOnSuccessListener { snapshot ->
            val badges = snapshot.children.mapNotNull { it.getValue(BadgeAchievedData::class.java) }
            onResult(badges)
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(null)
        }
    }

    fun loadAllBadge(onResult: (List<BadgeData>?) -> Unit) {
        val badgeRef = FirebaseDB.getMDbRef().child("badge")

        badgeRef.get().addOnSuccessListener { dataSnapshot ->
            val badgeList = mutableListOf<BadgeData>()
            for (child in dataSnapshot.children) {
                val badge = child.getValue(BadgeData::class.java)
                badge?.let {
                    it.id = child.key // assegna l'ID Firebase al campo `id` del BadgeData
                    badgeList.add(it)
                }
            }
            onResult(badgeList)
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            onResult(null)
        }
    }

    fun loadBadge(index: Int, onResult: (BadgeData?) -> Unit) {
        loadAllBadge { badgeList ->
            if (badgeList != null && index in badgeList.indices) {
                onResult(badgeList[index])
            } else {
                onResult(null)
            }
        }
    }
}