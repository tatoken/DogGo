package com.example.doggo_ourapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.doggo_ourapp.chat.Chat
import com.example.doggo_ourapp.databinding.MainAppLayoutBinding
import com.example.doggo_ourapp.diet.Food
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch


class MainApp : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var binding: MainAppLayoutBinding

    private lateinit var homeNavbarFooterButton: ImageButton
    private lateinit var calendarNavbarFooterButton: ImageButton
    private lateinit var foodNavbarFooterButton: ImageButton
    private lateinit var trophyNavbarFooterButton: ImageButton
    private lateinit var gymNavbarFooterButton: ImageButton
    private lateinit var chatNavbarFooterButton: ImageButton
    private lateinit var profileImage:ImageView

    private lateinit var openDrawerButton:ImageButton

    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = MainAppLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpNavbarHeaderButton()
        setUpNavbarFooterButton()
        setUpOffcanvasMenu()

    }

    override fun onStart() {
        super.onStart()

        DogFirebase.loadAllDog { dogList ->
            if (dogList.isNullOrEmpty()) {
                // Nessun cane → reindirizza alla schermata per aggiungerlo
                startActivity(Intent(this, AddDog::class.java))
                finish()
            } else {
                // Se c'è almeno un cane ma nessuno è selezionato
                DogFirebase.getActualDog { actualDogId ->
                    if (actualDogId == null) {
                        DogFirebase.selectDog(dogList.first().id!!) {
                            Toast.makeText(this, "Dog automatically selected", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun setUpOffcanvasMenu() {

        drawerLayout = findViewById(R.id.drawerLayout)
        openDrawerButton = findViewById(R.id.open_drawer_button)
        navView=findViewById(R.id.navView)

        openDrawerButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        UserFirebase.getCurrentUser() { result ->
            if (result?.administrator == "1") {
                navView.menu.findItem(R.id.adminItem)?.isVisible = true
            } else {
                navView.menu.findItem(R.id.adminItem)?.isVisible = false
            }
        }

        navView.setNavigationItemSelectedListener {

            when(it.itemId) {
                R.id.settingsItem -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                R.id.adminItem -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    val intent = Intent(this, TestActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }

            true
        }

    }

    private fun setUpNavbarHeaderButton() {
        profileImage = findViewById(R.id.profileImage);

        UserFirebase.getCurrentUser(){
            user->
            if(user!=null)
            {
                if(user.photo!=null)
                {
                    lifecycleScope.launch {
                        profileImage.setImageBitmap(
                            SupabaseManager.downloadImage(
                                "profile-image",
                                "${user.photo}"
                            )
                        )
                    }
                }
                else
                {
                    profileImage.setImageResource(R.drawable.blanck_people)
                }
            }
            else
            {
                profileImage.setImageResource(R.drawable.blanck_people)
            }
        }

        profileImage.setOnClickListener{
            resetButtonIcons()
            replaceFragment(ProfilePage())
        }
    }

    private fun setUpNavbarFooterButton() {
        homeNavbarFooterButton=binding.navHome
        calendarNavbarFooterButton=binding.navCalendar
        foodNavbarFooterButton=binding.navFood
        trophyNavbarFooterButton=binding.navTrophy
        gymNavbarFooterButton=binding.navGym
        chatNavbarFooterButton=binding.navChat

        homeNavbarFooterButton.setOnClickListener{
            resetButtonIcons()
            homeNavbarFooterButton.setImageResource(R.drawable.home_dark_24)
            replaceFragment(Homepage())
        }

        calendarNavbarFooterButton.setOnClickListener{
            resetButtonIcons()
            calendarNavbarFooterButton.setImageResource(R.drawable.calendar_black_24)
            replaceFragment(Calendar())
        }

        foodNavbarFooterButton.setOnClickListener{
            resetButtonIcons()
            foodNavbarFooterButton.setImageResource(R.drawable.food_black_24)
            replaceFragment(Food())
        }

        trophyNavbarFooterButton.setOnClickListener{
            resetButtonIcons()
            trophyNavbarFooterButton.setImageResource(R.drawable.trophy_black_24)
            replaceFragment(Trophy())
        }

        gymNavbarFooterButton.setOnClickListener{
            resetButtonIcons()
            gymNavbarFooterButton.setImageResource(R.drawable.gym_black_24)
            replaceFragment(Gym())
        }

        chatNavbarFooterButton.setOnClickListener{
            resetButtonIcons()
            chatNavbarFooterButton.setImageResource(R.drawable.baseline_chat_24)
            replaceFragment(Chat())
        }

        replaceFragment(Homepage())
        homeNavbarFooterButton.setImageResource(R.drawable.home_dark_24)
    }

    private fun resetButtonIcons() {
        homeNavbarFooterButton.setImageResource(R.drawable.home_white_24)
        calendarNavbarFooterButton.setImageResource(R.drawable.calendar_white_24)
        foodNavbarFooterButton.setImageResource(R.drawable.food_white_24)
        trophyNavbarFooterButton.setImageResource(R.drawable.trophy_white_24)
        gymNavbarFooterButton.setImageResource(R.drawable.gym_white_24)
        chatNavbarFooterButton.setImageResource(R.drawable.baseline_chat_bubble_outline_24)
    }

    fun replaceFragment(fragment: Fragment){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frgContainer,fragment)
        fragmentTransaction.commit()
    }
}
