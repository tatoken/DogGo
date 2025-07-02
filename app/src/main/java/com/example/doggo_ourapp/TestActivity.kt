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
            FirebaseDB.saveDog(DogData("0","Billo","Bastardino","Male","15","2312312312","80","030123123"))
        }

        loadDogButton=findViewById(R.id.loadDog)
        infoDog=findViewById(R.id.dogInfo)

        loadDogButton.setOnClickListener()
        {
            FirebaseDB.loadDog(0) { dog ->
                if (dog != null) {
                    infoDog.text="Nome: ${dog.name}, Razza: ${dog.breed}"
                } else {
                    infoDog.text="Errore"
                }
            }

        }

    }
}