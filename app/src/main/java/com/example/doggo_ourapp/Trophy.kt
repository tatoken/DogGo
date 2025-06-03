package com.example.doggo_ourapp

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator



class Trophy: Fragment(R.layout.trophy_layout) {

    private lateinit var progress_bar_section: LinearLayout
    private lateinit var badge_section: LinearLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: ImageSliderAdapter




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupImageSlider(view)
        setupClickableObject(view)
    }

    private fun setupClickableObject(view: View) {
        progress_bar_section=view.findViewById(R.id.progress_bar_section)
        badge_section=view.findViewById(R.id.badge_section)

        progress_bar_section.setOnClickListener(
            {
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frgContainer,Challenge())
                fragmentTransaction.commit()
            }
        )

        badge_section.setOnClickListener(
            {
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frgContainer,Badge())
                fragmentTransaction.commit()
            }
        )
    }

    private fun setupImageSlider(view:View) {
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)

        val images = listOf(
            R.drawable.badge_compass,
            R.drawable.badge_cat,
            R.drawable.badge_grape
        )

        adapter = ImageSliderAdapter(images)
        viewPager.adapter = adapter

        for (i in 0 until tabLayout.tabCount) {
            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(8, 0, 8, 0) // margine tra i puntini
            tab.requestLayout()
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, _ ->
            tab.text = null
        }.attach()
    }

}