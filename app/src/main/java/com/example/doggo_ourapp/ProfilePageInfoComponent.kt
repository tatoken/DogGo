package com.example.doggo_ourapp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageButton

class ProfilePageInfoComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val labelTextView: TextView
    private val valueTextView: TextView
    private val actionButton: ImageButton

    init {
        LayoutInflater.from(context).inflate(R.layout.profile_page_info_component, this, true)

        labelTextView = findViewById(R.id.label)
        valueTextView = findViewById(R.id.value)
        actionButton = findViewById(R.id.actionButton)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ProfilePageInfoComponent)
            labelTextView.text = typedArray.getString(R.styleable.ProfilePageInfoComponent_labelText) ?: ""
            valueTextView.text = typedArray.getString(R.styleable.ProfilePageInfoComponent_valueText) ?: ""
            typedArray.recycle()
        }
    }

    fun setLabel(text: String) {
        labelTextView.text = text
    }

    fun setValue(text: String) {
        valueTextView.text = text
    }

    fun setOnButtonClick(listener: OnClickListener) {
        actionButton.setOnClickListener(listener)
    }
}
