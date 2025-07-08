package com.example.doggo_ourapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.doggo_ourapp.TrainingFirebase.saveTraining

class TestActivity : AppCompatActivity() {

    private lateinit var addDogButton: Button
    private lateinit var loadDogButton: Button
    private lateinit var infoDog:TextView
    private lateinit var getActualDog:Button
    private lateinit var selectActualDog:Button

    private lateinit var addTraining:Button
    private lateinit var seeAllTrainings:Button
    private lateinit var seeTraining:Button
    private lateinit var infoTraining:TextView

    private lateinit var addEvent:Button
    private lateinit var seeAllEvents:Button
    private lateinit var seeEvent:Button
    private lateinit var infoEvent:TextView

    private lateinit var checkBadgeButton: Button
    private lateinit var addBadgeButton: Button
    private lateinit var seeBadgeButton: Button
    private lateinit var badgeInfo:TextView

    private lateinit var addPrizeButton: Button
    private lateinit var getPrizeButton: Button
    private lateinit var seePrizeButton: Button
    private lateinit var prizeInfo:TextView

    private lateinit var addDietButton: Button
    private lateinit var loadDietButton: Button
    private lateinit var addRecipe:Button
    private lateinit var addDietRecipeButton: Button
    private lateinit var loadDietRecipeButton: Button
    private lateinit var infoDiet:TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        addDogButton=findViewById(R.id.addDog)

        addTraining=findViewById(R.id.addTraining)
        seeAllTrainings=findViewById(R.id.seeAllTrainings)
        seeTraining=findViewById(R.id.seeTraining)
        infoTraining=findViewById(R.id.trainingInfo)

        addEvent=findViewById(R.id.addEvent)
        seeAllEvents=findViewById(R.id.seeAllEvents)
        seeEvent=findViewById(R.id.seeEvent)
        infoEvent=findViewById(R.id.eventInfo)

        addBadgeButton=findViewById(R.id.addBadge)
        checkBadgeButton=findViewById(R.id.checkBadge)
        seeBadgeButton=findViewById(R.id.seeBadge)
        badgeInfo=findViewById(R.id.badgeInfo)

        addPrizeButton=findViewById(R.id.addPrize)
        getPrizeButton=findViewById(R.id.getPrize)
        seePrizeButton=findViewById(R.id.seePrize)
        prizeInfo=findViewById(R.id.prizeInfo)

        getActualDog=findViewById(R.id.getActualDog)
        selectActualDog=findViewById(R.id.selectActualDog)

        addDogButton.setOnClickListener()
        {
            DogFirebase.saveDog(DogData(null,"Billo","Bastardino","Male","15","2312312312","80","030123123")) { result ->
                if (result) {
                    infoDog.text="Cane caricato"
                } else {
                    infoDog.text="Errore"
                }
            }
        }

        seeAllTrainings.setOnClickListener()
        {
            TrainingFirebase.loadAllTrainings { trainings ->
                if (trainings != null) {
                    val trainingInfos = StringBuilder()
                    var counter = 0

                    trainings.forEach { training ->
                        trainingInfos.append("Data:${training.date} - Km:${training.km}\n")
                        counter++
                        if (counter == trainings.size) {
                            infoTraining.text = trainingInfos.toString()
                        }
                    }
                } else {
                    infoTraining.text = "Errore"
                }
            }

        }

        seeTraining.setOnClickListener()
        {
            TrainingFirebase.loadTraining("-OUJuyAdcem7wm3Hd8dX"){ training ->
                if (training!=null) {
                    infoTraining.text="Data:${training.date} - Km:${training.km}\n"
                } else {
                    infoTraining.text="Errore"
                }
            }
        }

        addTraining.setOnClickListener()
        {
            TrainingFirebase.saveTraining(TrainingData(null,"culo","Ottimo","12:20","1.8")){ result ->
                if (result) {
                    infoDog.text="Training caricato"
                } else {
                    infoDog.text="Errore"
                }
            }
        }

        seeAllEvents.setOnClickListener()
        {
            EventFirebase.loadAllEvents { events ->
                if (events != null) {
                    val eventsInfos = StringBuilder()
                    var counter = 0

                    events.forEach { event ->
                        eventsInfos.append("Nome:${event.name} - Data:${event.date}\n")
                        counter++
                        if (counter == events.size) {
                            infoEvent.text = eventsInfos.toString()
                        }
                    }
                } else {
                    infoEvent.text = "Errore"
                }
            }

        }

        seeEvent.setOnClickListener()
        {
            EventFirebase.loadEvent("-OULqA6JudNgEeihdZDz"){ event ->
                if (event!=null) {
                    infoEvent.text="Nome:${event.name} - Data:${event.date}\n"
                } else {
                    infoEvent.text="Errore"
                }
            }
        }

        addEvent.setOnClickListener()
        {
            EventFirebase.saveEvent(EventData(null,"Passeggiata cane","04-07-2025","18:00","Training","Stasera allenamento")){ result ->
                if (result) {
                    infoEvent.text="Evento caricato"
                } else {
                    infoEvent.text="Errore"
                }
            }
        }

        loadDogButton=findViewById(R.id.loadDog)
        infoDog=findViewById(R.id.dogInfo)



        getActualDog.setOnClickListener()
        {
            DogFirebase.getActualDog () { dog ->
                if (dog != null) {
                    infoDog.text="ID actual dog: ${dog}"
                } else {
                    infoDog.text="Errore"
                }
            }

        }

        selectActualDog.setOnClickListener()
        {
            DogFirebase.selectDog ("-OU9JYKmtiQQUxkHcxoW") { result ->
                if (result) {
                    infoDog.text="cane selezionato"
                } else {
                    infoDog.text="Errore"
                }
            }
        }


        addBadgeButton.setOnClickListener()
        {

            BadgeFirebase.saveBadge(BadgeData(null,"Megadog","Do 5 km with your pet","5","trainingDistance")) { result ->
                if (result) {
                    badgeInfo.text ="Badge aggiunta"
                }
                else {
                    badgeInfo.text="Errore"
                }
            }

            BadgeFirebase.saveBadge(BadgeData(null,"Ultradog","Do 10 km with your pet","10","trainingDistance")) { result ->
                if (result) {
                    badgeInfo.text ="Badge aggiunta"
                }
                else {
                    badgeInfo.text="Errore"
                }
            }

            BadgeFirebase.saveBadge(BadgeData(null,"Gigadog","Do 20 km with your pet","20","trainingDistance")) { result ->
                if (result) {
                    badgeInfo.text ="Badge aggiunta"
                }
                else {
                    badgeInfo.text="Errore"
                }
            }
        }

        checkBadgeButton.setOnClickListener()
        {

            BadgeFirebase.checkAndAssignBadgesByType("trainingDistance","11") { badges ->
                if (badges != null) {
                    val badgeNames = StringBuilder()
                    badges.forEach { badge ->
                        badge.let {
                            badgeNames.append("• $it\n")
                        }
                    }
                    badgeInfo.text = badgeNames.toString()
                }
                else {
                    badgeInfo.text="Errore"
                }
            }
        }
        seeBadgeButton.setOnClickListener {
            BadgeFirebase.getUserBadges() { badges ->
                if (badges != null) {
                    val badgeNames = StringBuilder()
                    var counter = 0

                    badges.forEach { badgeAchieved ->
                        BadgeFirebase.getBadgeById(badgeAchieved.idBadge ?: "") { badgeData ->
                            if (badgeData != null) {
                                badgeNames.append("• ${badgeData.name} - ${badgeAchieved.achievedDate}\n")
                            }

                            counter++
                            if (counter == badges.size) {
                                badgeInfo.text = badgeNames.toString()
                            }
                        }
                    }
                } else {
                    badgeInfo.text = "Errore"
                }
            }
        }


        addPrizeButton.setOnClickListener()
        {
            PrizeFirebase.savePrize(PrizeData(null,"3Kg di crocchette","3Kg di ottime crocchette di qualità","100")) { result ->
                if (result) {
                    infoDog.text="Premio aggiunto"
                } else {
                    infoDog.text="Errore"
                }
            }
        }

        getPrizeButton.setOnClickListener()
        {
            PrizeFirebase.getPrize("-OUIlEEqoJm7-mAyn6RW") { result ->
                infoDog.text=result
            }
        }

        seePrizeButton.setOnClickListener {
            PrizeFirebase.getUserPrizes { prizes ->
                if (prizes != null) {
                    val prizeNames = StringBuilder()
                    var counter = 0

                    prizes.forEach { prizeAchieved ->
                        PrizeFirebase.getPrizeById(prizeAchieved.idPrize ?: "") { prizeData ->
                            if (prizeData != null) {
                                prizeNames.append("• ${prizeData.name} - ${prizeAchieved.quantity}\n")
                            }

                            counter++
                            if (counter == prizes.size) {
                                infoDog.text = prizeNames.toString()
                            }
                        }
                    }
                } else {
                    infoDog.text = "Errore"
                }
            }
        }


        addDietButton=findViewById(R.id.addDiet)

        /**********************************/


        addDietButton.setOnClickListener {

            DietFirebase.saveDiet(
                DietData(
                    "Dieta Bilanciata per Cani Adulti",
                    "35", "15", "25", "6", "Vitamina A, E, Calcio"
                )
            ) { success ->
                if (success) {
                    Toast.makeText(this, "Dieta salvata!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Errore nel salvataggio", Toast.LENGTH_SHORT).show()
                }
            }
        }

        loadDietButton=findViewById(R.id.loadDiet)
        infoDiet=findViewById(R.id.dietInfo)

        loadDietButton.setOnClickListener()
        {
            DietFirebase.loadDiet() { diet ->
                if (diet != null) {
                    infoDiet.text="Nome: ${diet.name}, Specifiche: ${diet.carbohydrates}, ${diet.vitamins}"
                } else {
                    infoDiet.text="Errore"
                }
            }
        }

        addRecipe=findViewById(R.id.addRecipe)
        addRecipe.setOnClickListener()
        {
            DietFirebase.saveRecipe(RecipeData(null, "Tiramisu","20","Dolce buono","2","Basso","10","10","10","10","10"))
            { success ->
                if (success) {
                    Toast.makeText(this, "Ricetta salvata!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Errore nel salvataggio", Toast.LENGTH_SHORT).show()
                }
            }
        }

        addDietRecipeButton=findViewById(R.id.addDietRecipe)

        addDietRecipeButton.setOnClickListener()
        {
            DietFirebase.saveDietRecipe(DietRecipeData(null,"-OUU2HqICauZ9EO1YG_I", "03/07/2025"))
            { success ->
                if (success) {
                    Toast.makeText(this, "Dieta-Ricetta salvata!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Errore nel salvataggio", Toast.LENGTH_SHORT).show()
                }
            }
        }

        loadDietRecipeButton=findViewById(R.id.loadDietRecipe)
        infoDiet=findViewById(R.id.dietInfo)

        loadDietRecipeButton.setOnClickListener()
        {
            DietFirebase.loadDietRecipe("-OUU5-HOqpsNGEskRgk6") { recipeDiet ->
                if (recipeDiet != null) {
                    DietFirebase.loadRecipeById(recipeDiet.idRecipe!!) { recipe ->
                        if(recipe!=null)
                        {
                            infoDiet.text="Name Recipe: ${recipe.name}, Data: ${recipeDiet.lastDataDone}"
                        }
                        else
                        {
                            infoDiet.text="Errore"
                        }
                    }
                } else {
                    infoDiet.text="Errore"
                }
            }

        }


    }
}