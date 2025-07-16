package com.example.doggo_ourapp.diet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.doggo_ourapp.ProfilePage
import com.example.doggo_ourapp.ProfilePageInfoComponent
import com.example.doggo_ourapp.R

class AddDiet : AppCompatActivity() {

    private lateinit var carbsInfo: ProfilePageInfoComponent
    private lateinit var fatsInfo: ProfilePageInfoComponent
    private lateinit var proteinsInfo: ProfilePageInfoComponent
    private lateinit var fibersInfo: ProfilePageInfoComponent
    private lateinit var vitaminsInfo: ProfilePageInfoComponent

    private lateinit var btnSaveDiet: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_diet)

        val carbsInfo = findViewById<ProfilePageInfoComponent>(R.id.carbs_info)
        val fatsInfo = findViewById<ProfilePageInfoComponent>(R.id.fats_info)
        val proteinsInfo = findViewById<ProfilePageInfoComponent>(R.id.proteins_info)
        val fibersInfo = findViewById<ProfilePageInfoComponent>(R.id.fibers_info)
        val vitaminsInfo = findViewById<ProfilePageInfoComponent>(R.id.vitamins_info)
        val btnSaveDiet = findViewById<Button>(R.id.btn_save_diet)

        // Abilita la modifica se necessario
        listOf(carbsInfo, fatsInfo, proteinsInfo, fibersInfo, vitaminsInfo).forEach {
            it.setEditable(true)
        }

        btnSaveDiet.setOnClickListener {
            val components = listOf(carbsInfo, fatsInfo, proteinsInfo, fibersInfo, vitaminsInfo)
            var allValid = true
            components.forEach {
                if (!it.validate()) allValid = false
            }

            if (!allValid) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val diet = DietData(
                carbohydrates = carbsInfo.getValue(),
                fats = fatsInfo.getValue(),
                proteins = proteinsInfo.getValue(),
                fibers = fibersInfo.getValue(),
                vitamins = vitaminsInfo.getValue()
            )

            DietFirebase.saveDiet(diet) { success ->
                if (success) {
                    Toast.makeText(this, "Diet successfully saved", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error saving diet", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        return carbsInfo.validate() &&
                fatsInfo.validate() &&
                proteinsInfo.validate() &&
                fibersInfo.validate() &&
                vitaminsInfo.validate()
    }
}

