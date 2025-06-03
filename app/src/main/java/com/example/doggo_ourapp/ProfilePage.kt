package com.example.doggo_ourapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth


class ProfilePage: Fragment(R.layout.profile_page_layout) {

    var textChange:TextView?=null
    var name_info:ProfilePageInfoComponent?=null
    lateinit var logoutButton:Button
    private lateinit var mAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textChange=view.findViewById(R.id.textChange)
        logoutButton=view.findViewById(R.id.btnLogout)

        name_info = view.findViewById<ProfilePageInfoComponent>(R.id.name_info)
        name_info?.setOnButtonClick(
            {
                textChange?.setText("Hai premuto il name")
            }
        )

        mAuth= FirebaseAuth.getInstance()

        logoutButton.setOnClickListener {
            mAuth.signOut()

            Toast.makeText(
                requireContext(),
                "Logout done",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(requireActivity(), Login::class.java)
            startActivity(intent)
            requireActivity().finish()
        }



    }

}