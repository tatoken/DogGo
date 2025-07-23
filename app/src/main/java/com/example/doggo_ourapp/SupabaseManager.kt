package com.example.doggo_ourapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SupabaseManager {
    private const val SUPABASE_URL = "https://axkdeohvorpiwoygumuv.supabase.co"
    private const val SUPABASE_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImF4a2Rlb2h2b3JwaXdveWd1bXV2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDk3MjQxOTUsImV4cCI6MjA2NTMwMDE5NX0.3YVtfHxTygFSqORUBzgChqbINxXhCJ6Ayf4xQMt21Vg"

    val client: SupabaseClient by lazy {
        try {
            createSupabaseClient(
                supabaseUrl = SUPABASE_URL,
                supabaseKey = SUPABASE_KEY
            ) {
                install(Storage)
            }.also {
                Log.d("Supabase", "Client creato correttamente")
            }
        } catch (e: Exception) {
            Log.e("Supabase", "Errore durante la creazione del client: ${e.localizedMessage}")
            throw RuntimeException("Errore nella configurazione Supabase", e)
        }
    }

    /**
     * Carica un'immagine. Se esiste già, la sovrascrive automaticamente.
     */
    fun uploadImage(bucketName: String, fileName: String, byteArray: ByteArray) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bucket = client.storage.from(bucketName)

                // Prova upload normale con upsert = false (non sovrascrive)
                bucket.upload(
                    path = fileName,
                    data = byteArray
                ) {
                    upsert = false
                }

                Log.d("Supabase", "Upload riuscito: $fileName")

            } catch (e: Exception) {
                val isAlreadyExistsError = e.message?.contains("The resource already exists", ignoreCase = true) == true

                if (isAlreadyExistsError) {
                    Log.w("Supabase", "File già esistente, sovrascrivo...")

                    updateImage(bucketName, fileName, byteArray)

                } else {
                    Log.e("Supabase", "Errore durante l'upload: ${e.localizedMessage}", e)
                }
            }
        }
    }

    /**
     * Sovrascrive un'immagine già esistente.
     */
    private suspend fun updateImage(bucketName: String, fileName: String, byteArray: ByteArray) {
        try {
            val bucket = client.storage.from(bucketName)

            bucket.upload(
                path = fileName,
                data = byteArray
            ) {
                upsert = true // Sovrascrive
            }

            Log.d("Supabase", "Update riuscito: $fileName")

        } catch (e: Exception) {
            Log.e("Supabase", "Errore durante l'update: ${e.localizedMessage}", e)
        }
    }

    /**
     * Scarica un'immagine pubblica come Bitmap.
     */
    suspend fun downloadImage(bucketName: String, fileName: String): Bitmap? {
        return try {
            val bucket = client.storage.from(bucketName)
            val bytes = bucket.downloadPublic(fileName)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            Log.e("Supabase error", "File download failed", e)
            null
        }
    }
}
