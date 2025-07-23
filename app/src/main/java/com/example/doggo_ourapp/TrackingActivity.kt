package com.example.doggo_ourapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import android.location.Location
import androidx.annotation.RequiresPermission
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class TrackingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var polyline: Polyline
    private var polylinePoints = mutableListOf<LatLng>()

    private lateinit var txtDistance: TextView
    private lateinit var chronometer: Chronometer
    private var distanceInMeters = 0f
    private var trackingStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        // Init UI
        txtDistance = findViewById(R.id.txt_distance)
        chronometer = findViewById(R.id.chronometer)
        val stopButton: Button = findViewById(R.id.btn_stop)

        // Init map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.tracking_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        stopButton.setOnClickListener {
            stopTracking()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        map.isMyLocationEnabled = true
        startTracking()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startTracking() {
        trackingStarted = true
        distanceInMeters = 0f
        polylinePoints.clear()

        polyline = map.addPolyline(PolylineOptions().color(resources.getColor(R.color.primaryColor)))

        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()

        val locationRequest = LocationRequest.create().apply {
            interval = 3000
            fastestInterval = 2000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val newPoint = LatLng(location.latitude, location.longitude)
                    polylinePoints.add(newPoint)
                    polyline.points = polylinePoints

                    // Move camera
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(newPoint, 17f))

                    // Update distance
                    if (polylinePoints.size > 1) {
                        val prev = polylinePoints[polylinePoints.size - 2]
                        val results = FloatArray(1)
                        Location.distanceBetween(
                            prev.latitude, prev.longitude,
                            newPoint.latitude, newPoint.longitude,
                            results
                        )
                        distanceInMeters += results[0]
                        txtDistance.text = String.format("%.2f km", distanceInMeters / 1000f)
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }

    private fun stopTracking() {
        if (!trackingStarted) return

        trackingStarted = false
        chronometer.stop()
        fusedLocationClient.removeLocationUpdates(locationCallback)

        // Calcolo tempo trascorso in millisecondi
        val elapsedMillis = SystemClock.elapsedRealtime() - chronometer.base

        // Converti in minuti e secondi
        val elapsedSeconds = (elapsedMillis / 1000).toInt()
        val minutes = elapsedSeconds / 60
        val seconds = elapsedSeconds % 60

        // Formatta tempo come "mm:ss"
        val elapsedTimeStr = String.format("%02d:%02d", minutes, seconds)

        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val nowHour = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val kmDone = (distanceInMeters + 100) / 1000f

        TrainingFirebase.saveTraining(TrainingData(null,today,nowHour,elapsedTimeStr,kmDone.toString()))
        {
            setResult(Activity.RESULT_OK)
            finish()
        }

    }

}