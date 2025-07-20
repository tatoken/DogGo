package com.example.doggo_ourapp

import PrizeScrollAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.launch


class Trophy: Fragment(R.layout.trophy_layout) {

    private lateinit var badge_section: LinearLayout
    private lateinit var badgeContainer: LinearLayout

    private lateinit var prize_section: LinearLayout

    private lateinit var score_section: LinearLayout



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickableObject(view)
    }

    private fun setupClickableObject(view: View) {
        badge_section=view.findViewById(R.id.badge_section)
        badgeContainer = view.findViewById(R.id.badgeContainer)

        prize_section=view.findViewById(R.id.prize_section)

        score_section=view.findViewById(R.id.scoreSection)

        badgeContainer.setOnClickListener(
            {
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frgContainer,Badge())
                fragmentTransaction.commit()
            }
        )

        prize_section.setOnClickListener(
            {
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frgContainer,Prizes())
                fragmentTransaction.commit()
            }
        )

        setUpPrizeSection(view)
        populateBadgeContainer()
        populateScoreContainer()
    }

    private fun setUpPrizeSection(view:View) {
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        val dotsIndicator = view.findViewById<DotsIndicator>(R.id.dotsIndicator)

        PrizeFirebase.loadAllPrizes() {prizes->
            if(prizes!=null)
            {
                val adapter = PrizeScrollAdapter(prizes,viewLifecycleOwner.lifecycleScope)
                viewPager.adapter = adapter

                dotsIndicator.setViewPager2(viewPager)
            }
        }
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

    private fun populateScoreContainer() {

        UserFirebase.loadTopUsers ()
        { topUsers->
            if (!topUsers.isNullOrEmpty()) {
                for (i in topUsers.indices) {
                    val scoreView = TrophyPageLeaderboardComponent(requireContext())
                    scoreView.setName(topUsers[i].name ?: "Anonimo")
                    scoreView.setPoints(topUsers[i].totalPoints)

                    val resourceName = "medal_${i + 1}"
                    val resId = requireContext().resources.getIdentifier(resourceName, "drawable", requireContext().packageName)

                    if (resId != 0) {
                        scoreView.setPositionImageSrc(resId)
                    } else {
                        Log.w("DrawableWarning", "Drawable not found for: $resourceName")
                    }

                    score_section.addView(scoreView)
                }
            }
        }

    }

}