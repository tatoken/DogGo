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

class TestActivity : AppCompatActivity() {

    private lateinit var addDogButton: Button
    private lateinit var loadDogButton: Button
    private lateinit var infoDog:TextView
    private lateinit var getActualDog:Button
    private lateinit var selectActualDog:Button

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

        addDietRecipeButton=findViewById(R.id.addDietRecipe)

        addDietRecipeButton.setOnClickListener()
        {
            DietFirebase.saveDietRecipe(DietRecipeData("0", "03/07/2025"))
            { success ->
                if (success) {
                    Toast.makeText(this, "Dieta-Ricetta salvata!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Errore nel salvataggio", Toast.LENGTH_SHORT).show()
                }
            }
        }

        loadDietRecipeButton=findViewById(R.id.loadDietRecipe)
        infoDietRecipe=findViewById(R.id.dietRecipeInfo)

        loadDietRecipeButton.setOnClickListener()
        {


        }


    }
}