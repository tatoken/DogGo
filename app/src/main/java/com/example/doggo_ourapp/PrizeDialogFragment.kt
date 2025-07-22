package com.example.doggo_ourapp

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class PrizeDialogFragment(
    private val prize: PrizeData,
    private val bitmap: Bitmap
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialogue_prize_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val name = view.findViewById<TextView>(R.id.dialogPrizeName)
        val description = view.findViewById<TextView>(R.id.dialogPrizeDescription)
        val threshold = view.findViewById<TextView>(R.id.dialogPrizeThreshold)
        val icon = view.findViewById<ImageView>(R.id.dialogPrizeIcon)
        val button = view.findViewById<Button>(R.id.dialogPrizeButton)

        name.text = prize.name
        description.text = prize.description
        threshold.text = "${prize.threshold} punti"
        icon.setImageBitmap(bitmap)

        button.setOnClickListener {
            PrizeFirebase.getPrize(prize.id!!) { result ->
                Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
                val bundle = Bundle().apply {
                    putString("prizeThreshold", prize.threshold)
                }
                parentFragmentManager.setFragmentResult("refresh_request", bundle)
                dismiss()
            }
        }
    }
}
