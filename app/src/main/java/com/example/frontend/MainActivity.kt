package com.example.frontend
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val mapFragment = SupportMapFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        // Initialize the map
        mapFragment.getMapAsync(this)

        // Set the initial fragment to the map fragment
        replaceFragment(mapFragment)

        // Handle BottomNavigationView item clicks
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> replaceFragment(mapFragment)
                // Add more cases for other navigation items
            }
            true
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
}
