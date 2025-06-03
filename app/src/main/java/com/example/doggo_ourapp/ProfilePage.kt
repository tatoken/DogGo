package com.example.doggo_ourapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment


class ProfilePage: Fragment(R.layout.profile_page_layout) {

    var textChange:TextView?=null
    var name_info:ProfilePageInfoComponent?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textChange=view.findViewById(R.id.textChange)

        name_info = view.findViewById<ProfilePageInfoComponent>(R.id.name_info)
        name_info?.setOnButtonClick(
            {
                textChange?.setText("Hai premuto il name")
            }
        )

    }

}