package com.example.doggo_ourapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.doggo_ourapp.SupabaseManager.downloadImage
import com.example.doggo_ourapp.UserFirebase.setPhotoOfUser
import com.example.doggo_ourapp.diet.AddDiet
import com.example.doggo_ourapp.diet.DietFirebase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfilePage : Fragment(R.layout.profile_page_layout) {

    // Firebase Auth instance
    private lateinit var mAuth: FirebaseAuth

    // Logout button
    private lateinit var logoutButton: Button

    // Personal section area
    private lateinit var profileImage: ImageView
    private lateinit var profileImageBig: ImageView
    private lateinit var name: TextView
    private lateinit var surname: TextView
    private lateinit var birthDate: TextView
    private lateinit var pickAPicture: Button
    private lateinit var userId:TextView

    private var imageBitmap: Bitmap? = null

    // InfoComponent groupings
    private lateinit var personalComponents: List<ProfilePageInfoComponent>
    private lateinit var dietComponents: List<ProfilePageInfoComponent>

    // Tracking which edit buttons are in "edit mode"
    private val editingStates = mutableMapOf<Int, Boolean>()

    // UI components diet-related (lateinit per uso su più metodi)
    private lateinit var btnInsertDiet: Button
    private lateinit var btnEditDiet: ImageButton
    private lateinit var carbsInfo: ProfilePageInfoComponent
    private lateinit var fatsInfo: ProfilePageInfoComponent
    private lateinit var proteinsInfo: ProfilePageInfoComponent
    private lateinit var fibersInfo: ProfilePageInfoComponent
    private lateinit var vitaminsInfo: ProfilePageInfoComponent

    /** -------------------- onViewCreated -------------------- **/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializza Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Setup Logout
        logoutButton = view.findViewById(R.id.btnLogout)
        logoutButton.setOnClickListener {
            mAuth.signOut()
            Toast.makeText(requireContext(), "Logout successful", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireActivity(), Login::class.java))
            requireActivity().finish()
        }

        /** -------------------- Personal Info Setup -------------------- **/

        profileImage = requireActivity().findViewById(R.id.profileImage)
        profileImageBig=view.findViewById(R.id.profileImageBig)
        name=view.findViewById(R.id.name)
        surname=view.findViewById(R.id.surname)
        birthDate=view.findViewById(R.id.birthDate)
        pickAPicture=view.findViewById(R.id.btnSelPic)
        userId=view.findViewById(R.id.userId)



        UserFirebase.getCurrentUser { user ->
            name.text = user?.name ?: ""
            surname.text= user?.surname ?: ""
            birthDate.text= user?.birthDate ?: ""
            userId.text=user?.uid?:""
            if(user?.photo==null)
            {
                profileImageBig.setImageResource(R.drawable.blanck_people)
                profileImage.setImageResource(R.drawable.blanck_people)
            }
            else
            {
                lifecycleScope.launch {
                    val bitmap = downloadImage("profile-image", user.photo!!)
                    profileImageBig.setImageBitmap(bitmap)
                    profileImage.setImageBitmap(bitmap)
                }
            }
        }

        pickAPicture.setOnClickListener {
            selectImageFromGallery()
        }

        /** -------------------- Dog Info Setup -------------------- **/
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
            if (!validateFields(personalComponents)) {
                Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setupEditButton
            }
            DogFirebase.updateDogPersonalFields(
                name = nameInfo.getValue(),
                breed = breedInfo.getValue(),
                sex = sexInfo.getValue(),
                age = ageInfo.getValue(),
                microchip = microchipInfo.getValue()
            ) { success ->
                showToast(success, "Dog updated successfully", "Error while saving dog data")
            }
        }

        /** -------------------- Diet Info Setup -------------------- **/


        btnInsertDiet = view.findViewById(R.id.btn_insert_diet)
        btnEditDiet = view.findViewById(R.id.btnEditDiet)

        btnInsertDiet.setOnClickListener {
            val intent = Intent(requireContext(), AddDiet::class.java)
            startActivity(intent)
        }

        carbsInfo = view.findViewById(R.id.carbs_info)
        fatsInfo = view.findViewById(R.id.fats_info)
        proteinsInfo = view.findViewById(R.id.proteins_info)
        fibersInfo = view.findViewById(R.id.fibers_info)
        vitaminsInfo = view.findViewById(R.id.vitamins_info)
        dietComponents = listOf(carbsInfo, fatsInfo, proteinsInfo, fibersInfo, vitaminsInfo)

        DietFirebase.checkIfDogHasDiet { hasDiet ->
            if (hasDiet) {
                btnInsertDiet.visibility = View.GONE
                btnEditDiet.visibility = View.VISIBLE

                setupEditButton(
                    btnEditDiet,
                    dietComponents
                ) {
                    if (!validateFields(dietComponents)) {
                        Toast.makeText(
                            requireContext(),
                            "Please fill all required fields",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setupEditButton
                    }
                    DietFirebase.updateDietFields(
                        carbohydrates = carbsInfo.getValue(),
                        fats = fatsInfo.getValue(),
                        proteins = proteinsInfo.getValue(),
                        fibers = fibersInfo.getValue(),
                        vitamins = vitaminsInfo.getValue()
                    ) { success ->
                        showToast(
                            success,
                            "Diet updated successfully",
                            "Error while saving diet data"
                        )
                    }
                }
                setDietComponentsVisible(true)

            } else {
                btnInsertDiet.visibility = View.VISIBLE
                btnEditDiet.visibility = View.GONE
                btnInsertDiet.setBackgroundResource(R.drawable.button_black_bg)
                setDietComponentsVisible(false)
            }
        }

        // Carica i dati iniziali del cane
        loadCurrentDogData()
    }

    /** -------------------- Photo picker section -------------------- **/

    private fun uploadImageOnSupabase() {
        if (imageBitmap == null) {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
            return
        }

        val byteArray = bitmapToByteArray(imageBitmap!!)

        lifecycleScope.launch {
            SupabaseManager.uploadImage("profile-image", "${UserFirebase.getCurrentUserId()}.jpeg", byteArray)
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    private fun selectImageFromGallery()
    {
        selectImageFromGalleryResult.launch("image/*")
    }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                profileImage.setImageURI(uri)
                profileImageBig.setImageURI(uri)
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                imageBitmap = BitmapFactory.decodeStream(inputStream)
                uploadImageOnSupabase()
                setPhotoOfUser(UserFirebase.getCurrentUserId()+".jpeg")
                inputStream?.close()
            }
        }

    /** -------------------- onResume: Ricarica stato dieta -------------------- **/
    override fun onResume() {
        super.onResume()
        DietFirebase.checkIfDogHasDiet { hasDiet ->
            updateDietUI(hasDiet)
            loadCurrentDogData()
        }
    }

    /** -------------------- UI Utility Methods -------------------- **/
    private fun updateDietUI(hasDiet: Boolean) {
        val visibility = if (hasDiet) View.VISIBLE else View.GONE
        btnInsertDiet.visibility = if (hasDiet) View.GONE else View.VISIBLE
        btnEditDiet.visibility = visibility
        setDietComponentsVisible(hasDiet)
    }

    private fun setDietComponentsVisible(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        carbsInfo.visibility = visibility
        fatsInfo.visibility = visibility
        proteinsInfo.visibility = visibility
        fibersInfo.visibility = visibility
        vitaminsInfo.visibility = visibility
    }

    private fun showToast(success: Boolean, successMsg: String, errorMsg: String) {
        Toast.makeText(requireContext(), if (success) successMsg else errorMsg, Toast.LENGTH_SHORT).show()
    }

    /** -------------------- Load current dog data -------------------- **/
    private fun loadCurrentDogData() {
        DogFirebase.getActualDog { dogId ->
            if (dogId == null) {
                Toast.makeText(requireContext(), "No dog selected", Toast.LENGTH_SHORT).show()
                return@getActualDog
            }

            DogFirebase.loadDog(dogId) { dogData ->
                if (dogData == null) {
                    Toast.makeText(requireContext(), "Failed to load dog data", Toast.LENGTH_SHORT).show()
                    return@loadDog
                }

                // Personal fields
                personalComponents[0].setValue(dogData.name ?: "")
                personalComponents[1].setValue(dogData.breed ?: "")
                personalComponents[2].setValue(dogData.sex ?: "")
                personalComponents[3].setValue(dogData.age ?: "")
                personalComponents[4].setValue(dogData.microchip ?: "")

                // Diet fields
                dietComponents[0].setValue(dogData.diet?.carbohydrates ?: "")
                dietComponents[1].setValue(dogData.diet?.fats ?: "")
                dietComponents[2].setValue(dogData.diet?.proteins ?: "")
                dietComponents[3].setValue(dogData.diet?.fibers ?: "")
                dietComponents[4].setValue(dogData.diet?.vitamins ?: "")
            }
        }
    }

    /** -------------------- Edit Button Setup -------------------- **/
    private fun setupEditButton(
        button: ImageButton,
        components: List<ProfilePageInfoComponent>,
        onSave: () -> Unit
    ) {
        val buttonId = button.id
        editingStates[buttonId] = false

        button.setOnClickListener {
            val isEditing = !(editingStates[buttonId] ?: false)

            // Se si sta uscendo dalla modalità di editing, salva
            if (!isEditing) {
                if (!validateFields(components)) {
                    Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                onSave()
            }

            components.forEach { it.setEditable(isEditing) }
            editingStates[buttonId] = isEditing

            button.setImageResource(
                if (isEditing) R.drawable.icons8_segno_di_spunta_24
                else R.drawable.icons8_modificare_24
            )
        }
    }

    /** -------------------- Field Validation -------------------- **/
    private fun validateFields(components: List<ProfilePageInfoComponent>): Boolean {
        return components.all { it.validate() }
    }
}
