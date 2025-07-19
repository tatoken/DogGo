package com.example.doggo_ourapp

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.doggo_ourapp.R

class TrophyPageBadgeComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val labelTextView: TextView
    private val imageImageButton: ImageButton

    init {
        // Gonfia il layout
        LayoutInflater.from(context).inflate(R.layout.trophy_page_badge_component, this, true)

        labelTextView = findViewById(R.id.label)
        imageImageButton = findViewById(R.id.image)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.TrophyPageBadgeComponent)
            try {
                labelTextView.text = typedArray.getString(R.styleable.TrophyPageBadgeComponent_trophyBadge_labelText) ?: ""
                val imageRes = typedArray.getResourceId(R.styleable.TrophyPageBadgeComponent_photoSrc, 0)
                if (imageRes != 0) {
                    imageImageButton.setImageResource(imageRes)
                }
            } finally {
                typedArray.recycle()
            }
        }
    }

    fun setLabel(text: String) {
        labelTextView.text = text
    }

    fun setImageSrcWithBitmap(bitmap: Bitmap) {
        imageImageButton.setImageBitmap(bitmap)
    }

    fun setImageSrcWithDrawable(drawable: Int) {
        imageImageButton.setImageResource(drawable)
    }

}
