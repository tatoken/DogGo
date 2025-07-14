package com.example.doggo_ourapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddDog : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_dog)

        val name = findViewById<ProfilePageInfoComponent>(R.id.name_info)
        val breed = findViewById<ProfilePageInfoComponent>(R.id.breed_info)
        val sex = findViewById<ProfilePageInfoComponent>(R.id.sex_info)
        val age = findViewById<ProfilePageInfoComponent>(R.id.age_info)
        val microchip = findViewById<ProfilePageInfoComponent>(R.id.microchip_info)
        val btnSave = findViewById<Button>(R.id.btn_save_dog)

        // Abilita la modifica se necessario
        listOf(name, breed, sex, age, microchip).forEach {
            it.setEditable(true)
        }

        btnSave.setOnClickListener {
            // Validazione campi obbligatori tramite funzione validate()
            val components = listOf(name, breed, sex, age, microchip)
            var allValid = true
            components.forEach {
                if (!it.validate()) allValid = false
            }
            if (!allValid) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ageValue = age.getValue()
            val microchipValue = microchip.getValue()

            // Controlla che Age sia un numero valido
            if (ageValue.toIntOrNull() == null) {
                age.setError("Age must be a valid number")
                //Toast.makeText(this, "Age must be a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Controlla che Microchip sia un numero valido
            if (microchipValue.toLongOrNull() == null) {
                microchip.setError("Microchip must be a valid number")
                //Toast.makeText(this, "Microchip must be a valid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dog = DogData(
                name = name.getValue(),
                breed = breed.getValue(),
                sex = sex.getValue(),
                age = ageValue,
                microchip = microchipValue
            )

            DogFirebase.saveDog(dog) { success ->
                if (success) {
                    DogFirebase.selectDog(dog.id!!) {
                        Toast.makeText(this, "Dog successfully added", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainApp::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Error while saving", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
