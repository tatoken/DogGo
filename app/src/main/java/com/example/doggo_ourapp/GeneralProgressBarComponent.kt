package com.example.doggo_ourapp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ProgressBar

class GeneralProgressBarComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val labelTextView: TextView
    private val progressBar: ProgressBar

    init {
        LayoutInflater.from(context).inflate(R.layout.general_progress_bar_component, this, true)

        labelTextView = findViewById(R.id.label)
        progressBar = findViewById(R.id.progressBar)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.GeneralProgressBarComponent)

            labelTextView.text = typedArray.getString(R.styleable.GeneralProgressBarComponent_progressBar_labelText) ?: ""

            progressBar.max = (typedArray.getString(R.styleable.GeneralProgressBarComponent_progressBar_upperBoundValueText) ?: "100").toInt()

            progressBar.progress = (typedArray.getString(R.styleable.GeneralProgressBarComponent_progressBar_valueText) ?: "0").toInt()

            typedArray.recycle()

        }
    }

    fun setLabel(text: String) {
        labelTextView.text = text
    }

    fun setProgressBarUpperBound(text: String) {
        progressBar.max = (text).toInt()
    }

    fun setProgressBarProgress(text: String) {
        progressBar.progress = (text).toInt()
    }


}
