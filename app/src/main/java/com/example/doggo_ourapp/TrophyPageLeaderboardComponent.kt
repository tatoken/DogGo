package com.example.doggo_ourapp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageButton
import android.widget.ImageView

class TrophyPageLeaderboardComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val positionImage: ImageView
    private val labelNameView: TextView
    private val valuePointsView: TextView
    private val valuePositionView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.trophy_page_leaderboard_component, this, true)

        positionImage = findViewById(R.id.positionImage)
        labelNameView = findViewById(R.id.name)
        valuePointsView = findViewById(R.id.points)
        valuePositionView=findViewById(R.id.position)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.TrophyPageLeaderboardComponent)
            try {
                labelNameView.text = typedArray.getString(R.styleable.TrophyPageLeaderboardComponent_trophyPosition_nameText) ?: ""
                valuePointsView.text = typedArray.getString(R.styleable.TrophyPageLeaderboardComponent_trophyPosition_pointText) ?: ""
                val image = typedArray.getResourceId(R.styleable.TrophyPageLeaderboardComponent_trophyPosition_photoSrc, 0)

                if (image != 0) {
                    positionImage.setImageResource(image)
                }
                else
                {
                    val parent = positionImage.parent as? ViewGroup
                    parent?.removeView(positionImage)

                    valuePositionView.text = typedArray.getString(R.styleable.TrophyPageLeaderboardComponent_trophyPosition_text) ?: "0"
                    val paramsPositionText = valuePositionView.layoutParams as LinearLayout.LayoutParams
                    paramsPositionText.weight = 3f
                    positionImage.layoutParams = paramsPositionText
                }

            } finally {
                typedArray.recycle()
            }
        }
    }

    fun setPositionImageSrc(resId: Int) {
        positionImage.setImageResource(resId)
    }

    fun setName(text: String) {
        labelNameView.text = text
    }

    fun setPoints(text: String) {
        valuePointsView.text = text
    }
}
