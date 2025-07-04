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
    private lateinit var getActualDog:Button
    private lateinit var selectActualDog:Button


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

        loadDogButton.setOnClickListener()
        {
            DogFirebase.loadDog("-OU9JYKmtiQQUxkHcxoW") { dog ->
                if (dog != null) {
                    infoDog.text="Nome: ${dog.name}, Razza: ${dog.breed}"
                } else {
                    infoDog.text="Errore"
                }
            }

        }

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

    }
}