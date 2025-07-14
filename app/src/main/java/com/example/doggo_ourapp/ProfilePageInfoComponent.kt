package com.example.doggo_ourapp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ImageButton


class ProfilePageInfoComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val labelTextView: TextView
    private val valueEditText: EditText

    init {
        LayoutInflater.from(context).inflate(R.layout.profile_page_info_component, this, true)
        orientation = VERTICAL

        labelTextView = findViewById(R.id.label)
        valueEditText = findViewById(R.id.value)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ProfilePageInfoComponent, 0, 0)

            val label = typedArray.getString(R.styleable.ProfilePageInfoComponent_labelText)
            val value = typedArray.getString(R.styleable.ProfilePageInfoComponent_valueText)

            labelTextView.text = label
            valueEditText.setText(value)

            typedArray.recycle()
        }

        setEditable(false)
    }

    fun setLabel(text: String) {
        labelTextView.text = text
    }

    fun setValue(text: String) {
        valueEditText.setText(text)
    }

    fun getValue(): String {
        return valueEditText.text.toString()
    }

    fun setEditable(editable: Boolean) {
        valueEditText.isEnabled = editable
        valueEditText.isFocusable = editable
        valueEditText.isFocusableInTouchMode = editable
        valueEditText.isCursorVisible = editable

        if (editable) {
            valueEditText.requestFocus()
        }
    }

    fun isEditable(): Boolean {
        return valueEditText.isEnabled
    }
}
