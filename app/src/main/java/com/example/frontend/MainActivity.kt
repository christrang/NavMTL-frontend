package com.example.frontend

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    // Update the map.JSON configuration at runtime.
    override fun onMapReady(googleMap: GoogleMap) {
        // Set the map.JSON coordinates to Montreal, Canada.
        val montreal = LatLng(45.5017, -73.5673)
        // Set the map.JSON type to Hybrid.
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Charger le style de carte personnalisé depuis le fichier JSON
        // Add a marker on the map.JSON coordinates.
        googleMap.addMarker(
            MarkerOptions()
                .position(montreal)
                .title("Montreal")
        )
        // Move the camera to the marker and zoom in.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(montreal, 11f))
        // Display traffic.
        googleMap.isTrafficEnabled = true
    }
}


