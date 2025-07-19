package com.example.doggo_ourapp

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch


class Trophy: Fragment(R.layout.trophy_layout) {

    private lateinit var progress_bar_section: LinearLayout

    private lateinit var badge_section: LinearLayout
    private lateinit var badgeContainer: LinearLayout




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickableObject(view)
    }

    private fun setupClickableObject(view: View) {
        progress_bar_section=view.findViewById(R.id.progress_bar_section)
        badge_section=view.findViewById(R.id.badge_section)
        badgeContainer = view.findViewById(R.id.badgeContainer)

        progress_bar_section.setOnClickListener(
            {
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frgContainer,Challenge())
                fragmentTransaction.commit()
            }
        )

        badgeContainer.setOnClickListener(
            {
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frgContainer,Badge())
                fragmentTransaction.commit()
            }
        )

        populateBadgeContainer()
    }

    private fun populateBadgeContainer() {

        BadgeFirebase.getUserBadges()
        { badges->
            if(badges!=null)
            {
                for (i in badges.indices) {
                    val badgeView = TrophyPageBadgeComponent(requireContext())

                    BadgeFirebase.getBadgeById(badges.get(i).idBadge!!)
                    {result->
                        if(result!=null)
                        {
                            badgeView.setLabel(result.name!!)
                            lifecycleScope.launch {
                                badgeView.setImageSrcWithBitmap(SupabaseManager.downloadImage("badge",result.name!!+".png")!!)

                                val layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                badgeView.layoutParams = layoutParams

                                badgeContainer.addView(badgeView)
                            }
                        }
                    }
                }

                if(badges.isEmpty())
                {
                    val badgeView = TrophyPageBadgeComponent(requireContext())
                    badgeView.setLabel("Niente di niente")
                    badgeView.setImageSrcWithDrawable(R.drawable.blanck_people)
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    badgeView.layoutParams = layoutParams
                    badgeContainer.addView(badgeView)
                }
            }
        }

    }


}