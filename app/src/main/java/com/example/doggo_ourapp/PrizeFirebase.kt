package com.example.doggo_ourapp

import java.time.LocalDate

object PrizeFirebase {

    fun getPrize(prizeId: String, onComplete: (String) -> Unit) {
        val dbRef = FirebaseDB.getMDbRef()
        val userId = UserFirebase.getCurrentUserId()
        val userRef = dbRef.child("user").child(userId)
        val prizeRef = dbRef.child("prize").child(prizeId)

        prizeRef.get().addOnSuccessListener { snapshotPrize ->
            if (!snapshotPrize.exists()) {
                onComplete("Errore database")
                return@addOnSuccessListener
            }

            val thresholdStr = snapshotPrize.child("threshold").getValue(String::class.java)
            val threshold = thresholdStr?.toIntOrNull() ?: run {
                onComplete("Errore generico")
                return@addOnSuccessListener
            }

            userRef.child("points").get().addOnSuccessListener { snapshotPoints ->
                val pointsStr = snapshotPoints.getValue(String::class.java)
                val currentPoints = pointsStr?.toIntOrNull() ?: run {
                    onComplete("Errore generico")
                    return@addOnSuccessListener
                }

                if (currentPoints < threshold) {
                    onComplete("Non hai abbastanza punti")
                    return@addOnSuccessListener
                }

                val prizeAchievedRef = userRef.child("prizeAchieved")

                prizeAchievedRef.get().addOnSuccessListener { snapshotAchieved ->
                    var prizeFound = false

                    for (child in snapshotAchieved.children) {
                        val idPrizeFromDb = child.child("idPrize").getValue(String::class.java)
                        if (idPrizeFromDb == prizeId) {
                            val currentQuantity = child.child("quantity").getValue(String::class.java)?.toIntOrNull() ?: 0
                            val newQuantity = (currentQuantity + 1).toString()

                            child.ref.child("quantity").setValue(newQuantity).addOnCompleteListener {
                                userRef.child("points").setValue((currentPoints - threshold).toString())
                                    .addOnCompleteListener {
                                        onComplete("Premio riscattato")
                                    }
                            }

                            prizeFound = true
                            break
                        }
                    }

                    if (!prizeFound) {
                        val newRef = prizeAchievedRef.push()
                        val newPrizeAchieved = PrizeAchievedData(
                            idPrize = snapshotPrize.child("id").getValue(String::class.java),
                            achieveDate = LocalDate.now().toString(),
                            quantity = "1"
                        )

                        newRef.setValue(newPrizeAchieved).addOnCompleteListener {
                            userRef.child("points").setValue((currentPoints - threshold).toString())
                                .addOnCompleteListener {
                                    onComplete("Premio riscattato")
                                }
                        }
                    }

                }.addOnFailureListener {
                    onComplete("Errore database")
                }

            }.addOnFailureListener {
                onComplete("Non hai abbastanza punti")
            }

        }.addOnFailureListener {
            onComplete("Errore database")
        }
    }


    fun getUserPrizes(onResult: (List<PrizeAchievedData>?) -> Unit) {
        val userRef = FirebaseDB.getMDbRef().child("user").child(UserFirebase.getCurrentUserId()).child("prizeAchieved")

        userRef.get().addOnSuccessListener { snapshot ->
            val prizes = snapshot.children.mapNotNull { it.getValue(PrizeAchievedData::class.java) }
            onResult(prizes)
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(null)
        }
    }

    fun loadAllPrizes(onResult: (List<PrizeData>?) -> Unit) {
        val prizeRef = FirebaseDB.getMDbRef().child("prize")

        prizeRef.get().addOnSuccessListener { dataSnapshot ->
            val prizeList = mutableListOf<PrizeData>()
            for (child in dataSnapshot.children) {
                val prize = child.getValue(PrizeData::class.java)
                prize?.let {
                    it.id = child.key // assegna l'ID Firebase al campo `id` del BadgeData
                    prizeList.add(it)
                }
            }
            onResult(prizeList)
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            onResult(null)
        }
    }

    fun getPrizeById(prizeId: String?, onResult: (PrizeData?) -> Unit) {
        var prizeRef = FirebaseDB.getMDbRef().child("prize")
        if(prizeId!=null)
        {
            prizeRef =prizeRef.child(prizeId)
        }

        prizeRef.get().addOnSuccessListener { snapshot ->
            val prize = snapshot.getValue(PrizeData::class.java)
            onResult(prize)
        }.addOnFailureListener {
            it.printStackTrace()
            onResult(null)
        }
    }


    fun savePrize(prize: PrizeData, onResult: (Boolean) -> Unit) {

        val prizeRef = FirebaseDB.getMDbRef()
            .child("prize")
            .push()

        prize.id = prizeRef.key

        prizeRef.setValue(prize).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

}