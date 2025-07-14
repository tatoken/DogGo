package com.example.doggo_ourapp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class ProfilePageInfoComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val labelTextView: TextView
    private val valueEditText: EditText
    private var required: Boolean = false   // memorizza se è obbligatorio

    init {
        LayoutInflater.from(context).inflate(R.layout.profile_page_info_component, this, true)
        orientation = VERTICAL

        labelTextView = findViewById(R.id.label)
        valueEditText = findViewById(R.id.value)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ProfilePageInfoComponent, 0, 0)

            val label = typedArray.getString(R.styleable.ProfilePageInfoComponent_labelText)
            val value = typedArray.getString(R.styleable.ProfilePageInfoComponent_valueText)
            required = typedArray.getBoolean(R.styleable.ProfilePageInfoComponent_required, false)

            labelTextView.text = label ?: ""
            valueEditText.hint = "Insert"

            typedArray.recycle()
        }

        // Se è obbligatorio aggiungi asterisco alla label
        if (required) {
            labelTextView.text = "${labelTextView.text} *"
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

    fun setError(errorMsg: String?) {
        valueEditText.error = errorMsg
    }

    // Funzione per validare campo obbligatorio
    fun validate(): Boolean {
        val text = getValue().trim()
        return if (required && text.isEmpty()) {
            valueEditText.error = "Required field"
            false
        } else {
            valueEditText.error = null
            true
        }
    }
}
