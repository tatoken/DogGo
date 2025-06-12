package com.example.doggo_ourapp

import android.graphics.BitmapFactory
import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

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

    fun uploadImage(bucketName: String, fileName: String, byteArray: ByteArray) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bucket = client.storage.from(bucketName)

                val response = bucket.upload(
                    path = fileName,
                    data = byteArray
                ) {
                    upsert = false
                }

            } catch (e: Exception) {
                Log.e("barbara", "File upload failed", e)
            }
        }
    }

}
