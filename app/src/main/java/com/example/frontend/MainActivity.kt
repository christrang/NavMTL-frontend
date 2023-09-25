package com.example.frontend

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.core.view.GravityCompat

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val mapFragment = SupportMapFragment.newInstance()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: Button


    private lateinit var token: String // Déclarez le token ici
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Récupérer le token depuis SharedPreferences dans la méthode onCreate
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        token = sharedPrefs.getString("token", null) ?: ""

        if (token.isNotEmpty()) {
            // Utilisez le token comme vous le souhaitez dans cette activité
            // Par exemple, vous pouvez l'afficher dans un TextView
            Log.d("LoginActivity", "Token: $token")
        }


        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)

        // Initialize the map
        mapFragment.getMapAsync(this)

        // Set the initial fragment to the map fragment
        replaceFragment(mapFragment)

        // Handle menu button click to open/close the navigation menu
        menuButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.map_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Set up the map configuration when it's ready
        val montreal = LatLng(45.5017, -73.5673)
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        googleMap.addMarker(
            MarkerOptions()
                .position(montreal)
                .title("Montreal")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(montreal, 11f))
        googleMap.isTrafficEnabled = true
    }

    // Handle menu item/button clicks here
    fun handleMenuItemClick(view: android.view.View) {
        when (view.id) {
            R.id.menu_item_1 -> {
                // Handle Menu Item 1 click
                Toast.makeText(this, "Menu Item 1 clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_item_2 -> {
                // Handle Menu Item 2 click
                Toast.makeText(this, "Menu Item 2 clicked", Toast.LENGTH_SHORT).show()
            }
            // Add more cases for other menu items/buttons as needed
        }
    }
}
