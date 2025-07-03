package com.example.doggo_ourapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TestActivity : AppCompatActivity() {

    private lateinit var addDogButton: Button
    private lateinit var loadDogButton: Button
    private lateinit var infoDog:TextView

    private lateinit var addDietButton: Button
    private lateinit var loadDietButton: Button
    private lateinit var infoDiet:TextView

    private lateinit var addDietRecipeButton: Button
    private lateinit var loadDietRecipeButton: Button
    private lateinit var infoDietRecipe:TextView

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

        addDogButton.setOnClickListener()
        {
            DogFirebase.saveDog(DogData("0","Billo","Bastardino","Male","15","2312312312","80","030123123"))
        }

        loadDogButton=findViewById(R.id.loadDog)
        infoDog=findViewById(R.id.dogInfo)

        loadDogButton.setOnClickListener()
        {
            DogFirebase.loadDog(0) { dog ->
                if (dog != null) {
                    infoDog.text="Nome: ${dog.name}, Razza: ${dog.breed}"
                } else {
                    infoDog.text="Errore"
                }
            }

        }

        addDietButton=findViewById(R.id.addDiet)

        addDietButton.setOnClickListener()
        {
            DietFirebase.saveDiet("-OUEpPegW0rbZGPP3Wua", DietData("Dieta Bilanciata per Cani Adulti", "35","15", "25","6","Vitamina A, E, Calcio"))
        }

        loadDietButton=findViewById(R.id.loadDiet)
        infoDiet=findViewById(R.id.dietInfo)

        loadDietButton.setOnClickListener()
        {
            DietFirebase.loadDiet("-OUEpPegW0rbZGPP3Wua") { diet ->
                if (diet != null) {
                    infoDiet.text="Nome: ${diet.name}, Specifiche: ${diet.carbohydrates}, ${diet.vitamins}"
                } else {
                    infoDiet.text="Errore"
                }
            }
        }

        addDietRecipeButton=findViewById(R.id.addDietRecipe)

        addDietRecipeButton.setOnClickListener()
        {
            DietFirebase.saveDietRecipe("-OUEpPegW0rbZGPP3Wua", DietRecipeData("0","03/07/2025"))
        }

        loadDietRecipeButton=findViewById(R.id.loadDietRecipe)
        infoDietRecipe=findViewById(R.id.dietRecipeInfo)

        loadDietRecipeButton.setOnClickListener()
        {
            DietFirebase.loadDietRecipe("-OUEpPegW0rbZGPP3Wua",0) { dietRecipe ->
                if (dietRecipe != null) {
                    infoDietRecipe.text="Nome: ${dietRecipe.idRecipe}, LastDone: ${dietRecipe.lastDataDone}"
                } else {
                    infoDietRecipe.text="Errore"
                }
            }

        }


    }
}