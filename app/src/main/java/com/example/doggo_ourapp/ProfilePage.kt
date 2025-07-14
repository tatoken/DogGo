package com.example.doggo_ourapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.doggo_ourapp.diet.DietFirebase
import com.google.firebase.auth.FirebaseAuth


class ProfilePage : Fragment(R.layout.profile_page_layout) {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var logoutButton: Button

    private lateinit var personalComponents: List<ProfilePageInfoComponent>
    private lateinit var healthComponents: List<ProfilePageInfoComponent>
    private lateinit var dietComponents: List<ProfilePageInfoComponent>

    private val editingStates = mutableMapOf<Int, Boolean>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        logoutButton = view.findViewById(R.id.btnLogout)
        logoutButton.setOnClickListener {
            mAuth.signOut()
            Toast.makeText(requireContext(), "Logout done", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireActivity(), Login::class.java))
            requireActivity().finish()
        }

        val nameInfo = view.findViewById<ProfilePageInfoComponent>(R.id.name_info)
        val breedInfo = view.findViewById<ProfilePageInfoComponent>(R.id.breed_info)
        val sexInfo = view.findViewById<ProfilePageInfoComponent>(R.id.sex_info)
        val ageInfo = view.findViewById<ProfilePageInfoComponent>(R.id.age_info)
        val microchipInfo = view.findViewById<ProfilePageInfoComponent>(R.id.microchip_info)
        personalComponents = listOf(nameInfo, breedInfo, sexInfo, ageInfo, microchipInfo)

        setupEditButton(
            view.findViewById(R.id.btnEditPersonal),
            personalComponents
        ) {
            DogFirebase.updateDogPersonalFields(
                name = nameInfo.getValue(),
                breed = breedInfo.getValue(),
                sex = sexInfo.getValue(),
                age = ageInfo.getValue(),
                microchip = microchipInfo.getValue()
            ){ success ->
                val msg = if (success) "Dog updated successfully" else "Error while saving dog"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        val weightInfo = view.findViewById<ProfilePageInfoComponent>(R.id.weight_info)
        val vaccinationInfo = view.findViewById<ProfilePageInfoComponent>(R.id.vaccinations_info)
        val allergiesInfo = view.findViewById<ProfilePageInfoComponent>(R.id.allergies_info)
        val interventionsInfo = view.findViewById<ProfilePageInfoComponent>(R.id.interventions_info)
        val treatmentsInfo = view.findViewById<ProfilePageInfoComponent>(R.id.treatments_info)
        healthComponents = listOf(weightInfo, vaccinationInfo, allergiesInfo, interventionsInfo, treatmentsInfo)

        setupEditButton(
            view.findViewById(R.id.btnEditHealth),
            healthComponents
        ) {
            DogFirebase.updateDogHealthFields(
                weight = weightInfo.getValue(),
                vaccinations = vaccinationInfo.getValue(),
                allergies = allergiesInfo.getValue(),
                interventions = interventionsInfo.getValue(),
                treatments = treatmentsInfo.getValue()
            ){ success ->
                val msg = if (success) "Dog updated successfully" else "Error while saving dog"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        val carbsInfo = view.findViewById<ProfilePageInfoComponent>(R.id.carbs_info)
        val fatsInfo = view.findViewById<ProfilePageInfoComponent>(R.id.fats_info)
        val proteinsInfo = view.findViewById<ProfilePageInfoComponent>(R.id.proteins_info)
        val fibersInfo = view.findViewById<ProfilePageInfoComponent>(R.id.fibers_info)
        val vitaminsInfo = view.findViewById<ProfilePageInfoComponent>(R.id.vitamins_info)
        dietComponents = listOf(carbsInfo, fatsInfo, proteinsInfo, fibersInfo, vitaminsInfo)

        setupEditButton(
            view.findViewById(R.id.btnEditDiet),
            dietComponents
        ) {
            DietFirebase.updateDietFields(
                carbohydrates = carbsInfo.getValue(),
                fats = fatsInfo.getValue(),
                proteins = proteinsInfo.getValue(),
                fibers = fibersInfo.getValue(),
                vitamins = vitaminsInfo.getValue()
            ) { success ->
                val msg = if (success) "Diet updated successfully" else "Error while saving diet"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        loadCurrentDogData()

    }

    private fun loadCurrentDogData() {
        DogFirebase.getActualDog { dogId ->
            if (dogId == null) {
                Toast.makeText(requireContext(), "No dog selected", Toast.LENGTH_SHORT).show()
                return@getActualDog
            }

            DogFirebase.loadDog(dogId) { dogData ->
                if (dogData == null) {
                    Toast.makeText(requireContext(), "Failed to load dog data", Toast.LENGTH_SHORT)
                        .show()
                    return@loadDog
                }

                // Aggiorna i componenti UI con i dati del cane
                personalComponents[0].setValue(dogData.name ?: "")
                personalComponents[1].setValue(dogData.breed ?: "")
                personalComponents[2].setValue(dogData.sex ?: "")
                personalComponents[3].setValue(dogData.age ?: "")
                personalComponents[4].setValue(dogData.microchip ?: "")

                healthComponents[0].setValue(dogData.weight ?: "")
                // Per vaccinazioni, allergie, ecc, devi mappare i dati da dogData agli UI componenti
                // Se dogData non ha quei campi, usa stringhe vuote o un placeholder
                //healthComponents[1].setValue("") // vaccinations info
                //healthComponents[2].setValue("") // allergies info
                //healthComponents[3].setValue("") // interventions info
                //healthComponents[4].setValue("") // treatments info

                // Per la dieta
                dietComponents[0].setValue(dogData.diet?.carbohydrates ?: "")
                dietComponents[1].setValue(dogData.diet?.fats ?: "")
                dietComponents[2].setValue(dogData.diet?.proteins ?: "")
                dietComponents[3].setValue(dogData.diet?.fibers ?: "")
                dietComponents[4].setValue(dogData.diet?.vitamins ?: "")
            }
        }
    }



    private fun setupEditButton(
        button: ImageButton,
        components: List<ProfilePageInfoComponent>,
        onSave: () -> Unit
    ) {
        // Usa ID del bottone come chiave per lo stato
        val buttonId = button.id
        editingStates[buttonId] = false

        button.setOnClickListener {
            val isEditing = !(editingStates[buttonId] ?: false)

            // Toggle editing
            components.forEach { it.setEditable(isEditing) }
            editingStates[buttonId] = isEditing

            if (isEditing) {
                button.setImageResource(R.drawable.icons8_segno_di_spunta_24)
            } else {
                button.setImageResource(R.drawable.icons8_modificare_24)
                onSave()
            }
        }
    }


}
