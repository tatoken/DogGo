package com.example.doggo_ourapp

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.doggo_ourapp.SupabaseManager.downloadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private var back_button: Button?=null
    private lateinit var btnSelPic: Button
    private lateinit var btnDwld: Button
    private lateinit var ivPic: ImageView
    private lateinit var dwldPic: ImageView
    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnSelPic = findViewById(R.id.btnSelPic)
        btnDwld=findViewById(R.id.btnDownload)
        ivPic = findViewById(R.id.ivPic)
        dwldPic=findViewById(R.id.dwldPic)

        btnSelPic.setOnClickListener {
            selectImageFromGallery()
        }

        btnDwld.setOnClickListener {
            downloadImageFromSupabase()
        }

        back_button=findViewById(R.id.back_button)
        back_button?.setOnClickListener{
            val intent= Intent(this, MainApp::class.java)
            startActivity(intent)
            finish()
        }


    }


    private fun uploadImageOnSupabase() {
        if (imageBitmap == null) {
            Toast.makeText(this, "Nessuna immagine selezionata", Toast.LENGTH_SHORT).show()
            return
        }

        val byteArray = bitmapToByteArray(imageBitmap!!)

        lifecycleScope.launch {
            Log.e("Supabase", "Inizio funzione upload")
            SupabaseManager.uploadImage("profile-image", "user-image.jpeg", byteArray)
            Log.e("Supabase", "Fine funzione upload")
        }
    }

    private fun downloadImageFromSupabase()
    {
        lifecycleScope.launch {
            val bitmap = downloadImage("profile-image", "user-image.jpeg")
            dwldPic.setImageBitmap(bitmap)
        }
    }


    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }



    private fun selectImageFromGallery()
    {
        selectImageFromGalleryResult.launch("image/*")
    }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                ivPic.setImageURI(uri)
                val inputStream = contentResolver.openInputStream(uri)
                imageBitmap = BitmapFactory.decodeStream(inputStream)
                uploadImageOnSupabase()
                inputStream?.close()
            }
        }

}