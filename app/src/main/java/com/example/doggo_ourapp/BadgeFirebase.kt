package com.example.doggo_ourapp

import java.time.LocalDate

object BadgeFirebase {

    fun checkAndAssignBadgesByType(
        type: String,
        value: String,
        onComplete: (List<String>) -> Unit
    ) {
            loadAllBadges { allBadges ->
            if (allBadges == null) {
                onComplete(emptyList())
                return@loadAllBadges
            }

            val filteredBadges = allBadges.filter { it.type == type }

            getUserBadges() { userBadges ->
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

    fun getUserBadges(onResult: (List<BadgeAchievedData>?) -> Unit) {
        val userRef = FirebaseDB.getMDbRef().child("user").child(UserFirebase.getCurrentUserId()).child("badgeAchieved")

        userRef.get().addOnSuccessListener { snapshot ->
            val badges = snapshot.children.mapNotNull { it.getValue(BadgeAchievedData::class.java) }
            onResult(badges)
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(null)
        }
    }

    fun loadAllBadges(onResult: (List<BadgeData>?) -> Unit) {
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

    fun getBadgeById(badgeId: String, onResult: (BadgeData?) -> Unit) {
        val badgeRef = FirebaseDB.getMDbRef().child("badge").child(badgeId)

        badgeRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val badgeData = snapshot.getValue(BadgeData::class.java)
                onResult(badgeData)
            } else {
                onResult(null)
            }
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(null)
        }
    }


    fun saveBadge(badge: BadgeData, onResult: (Boolean) -> Unit) {

        val badgeRef = FirebaseDB.getMDbRef()
            .child("badge")
            .push()

        badge.id = badgeRef.key

        badgeRef.setValue(badge).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }
}