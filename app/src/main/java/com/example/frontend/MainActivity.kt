package com.example.frontend

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.AutocompleteSupportFragment.*
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.GeoApiContext
import com.google.maps.DirectionsApi
import com.google.maps.android.SphericalUtil
import com.google.maps.model.DirectionsResult
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val mapFragment = SupportMapFragment.newInstance()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var bottomSheet: LinearLayout
    private var initialHeight = 500
    private lateinit var token: String
    private lateinit var placesClient: PlacesClient
    private lateinit var autoCompleteFragment: AutocompleteSupportFragment
    private lateinit var editTextAddress: EditText
    private lateinit var googleMap: GoogleMap // Ajout de la référence à GoogleMap

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.grey)

        bottomSheet = findViewById(R.id.bottom_sheet)
        initialHeight = resources.getDimensionPixelSize(R.dimen.initial_height)

        // Récupérer le token depuis SharedPreferences
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        token = sharedPrefs.getString("token", null) ?: ""

        drawerLayout = findViewById(R.id.drawer_layout)
        menuButton = findViewById(R.id.menu_button)

        // Initialize the map
        mapFragment.getMapAsync(this)
        replaceFragment(mapFragment)

        Places.initialize(applicationContext, "AIzaSyAUcUujvbKP4jVrmo3I00MNI8pdar4Ag0g") // Remplacez par votre clé API Google Maps
        val placesClient = Places.createClient(this)
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment)
                as AutocompleteSupportFragment

        // Specify location bias for autocomplete
        val montrealLatLngBounds = LatLngBounds(LatLng(45.4215, -73.5696), LatLng(45.6983, -73.4828))
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(montrealLatLngBounds))

        // Specify the types of place data to return (in this case, just addresses)
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        editTextAddress = findViewById(R.id.editTextAddress)

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val montreal = LatLng(45.5017, -73.5673)
                val selectedLatLng = place.latLng
                Log.e("place", place.toString())
                if (selectedLatLng != null) {
                    // Utilisez selectedLatLng pour obtenir les coordonnées (latitude et longitude)
                    getDirections(montreal, selectedLatLng)
                } else {
                    // La position sélectionnée n'a pas de coordonnées valides
                    Log.e("place", montreal.toString())
                    Toast.makeText(applicationContext, "Coordonnées non disponibles", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                val errorMessage = status.statusMessage
                Toast.makeText(applicationContext, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        })

        menuButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        val bottomSheetView = findViewById<LinearLayout>(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.peekHeight = 500
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.map_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap // Assigner la référence à googleMap

        val repository = Repository(application)
        val montreal = LatLng(45.5017, -73.5673)

        // Personnalisez le style de la carte ici en utilisant MapStyleOptions
        val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.map)
        googleMap.setMapStyle(mapStyleOptions)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(montreal, 17f))
        googleMap.isTrafficEnabled = false

        val distanceLimiteEnMetres = 500.0 // Définissez la distance limite en mètres

        // Liste pour stocker les panneaux proches de Montreal
        val panneauxProches = mutableListOf<Panneau>()
        val width = 40
        val height = 40

        // Charger l'image depuis les ressources
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.parking)

        // Redimensionner l'image à la taille souhaitée
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)

        // Créer un BitmapDescriptor à partir de l'image redimensionnée
        val customMarkerIcon = BitmapDescriptorFactory.fromBitmap(resizedBitmap)
        // Récupérez la liste des panneaux depuis votre API
        repository.getPanneaux(object : PanneauxCallback {
            override fun onSuccess(panneaux: MutableList<Panneau>) {
                // Filtrer les panneaux proches de Montreal
                for (panneau in panneaux) {
                    val panneauLocation =
                        LatLng(panneau.latitude.toDouble(), panneau.longitude.toDouble())

                    // Calculer la distance entre le panneau et Montreal
                    val distance = SphericalUtil.computeDistanceBetween(montreal, panneauLocation)

                    // Vérifier si la distance est inférieure ou égale à la distance limite
                    if (distance <= distanceLimiteEnMetres) {
                        // Ajouter le panneau à la liste des panneaux proches
                        if (panneau.DESCRIPTION_REP != "Enlevé") {
                            // Ajouter un marqueur pour le desrpa
                            val desrpaMarker = MarkerOptions()
                                .position(panneauLocation)
                                .title("DESRPA: ${panneau.description},${panneau.flechePan}")
                                .icon(customMarkerIcon)
                            googleMap.addMarker(desrpaMarker)

                    }
                    }
                }

            }

            override fun onError(errorMessage: String) {
                Toast.makeText(this@MainActivity, "Erreur : $errorMessage", Toast.LENGTH_SHORT).show()
                Log.e("Error Panneaux", errorMessage)
            }
        })
    }

    // Handle menu item/button clicks here
    fun handleMenuItemClick(view: View) {
        when (view.id) {
            R.id.menu_item_1 -> {
                Toast.makeText(this, "Menu Item 1 clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val markers: MutableList<Marker> = mutableListOf()
    private var currentPolyline: Polyline? = null
    private fun getDirections(startLatLng: LatLng, endLatLng: LatLng) {
        val context = GeoApiContext.Builder()
            .apiKey("AIzaSyAUcUujvbKP4jVrmo3I00MNI8pdar4Ag0g") // Remplacez par votre clé API Google Maps
            .build()

        val request = DirectionsApi.getDirections(
            context,
            "${startLatLng.latitude},${startLatLng.longitude}",
            "${endLatLng.latitude},${endLatLng.longitude}"
        )

        try {
            val result: DirectionsResult = request.await()

            // Supprimer les anciens marqueurs s'il y en a
            for (marker in markers) {
                marker.remove()
            }
            markers.clear()

            // Créer une liste de points pour la nouvelle polyline
            val polylineOptions = PolylineOptions()
            val steps = result.routes[0].legs[0].steps
            for (step in steps) {
                val points = step.polyline.decodePath()
                for (point in points) {
                    polylineOptions.add(LatLng(point.lat, point.lng))
                }
            }
            polylineOptions.width(20f)

            // Supprimer l'ancienne polyline s'il y en a une
            currentPolyline?.remove()

            // Ajouter la nouvelle polyline à la carte
            val newPolyline = googleMap.addPolyline(polylineOptions)

            // Mémoriser la nouvelle polyline comme étant la "currentPolyline"
            currentPolyline = newPolyline

            // ... (code pour les nouveaux marqueurs)
            // Ajouter les nouveaux marqueurs à la liste
            val originMarkerOptions = MarkerOptions()
                .position(startLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.forme_abstraite)) // Replace with your origin icon resource
                .title("Origin")

            val destinationMarkerOptions = MarkerOptions()
                .position(endLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.rhombe)) // Replace with your destination icon resource
                .title("Destination")

            val originMarker = googleMap.addMarker(originMarkerOptions)
            val destinationMarker = googleMap.addMarker(destinationMarkerOptions)

            if (originMarker != null) {
                markers.add(originMarker)
            }
            if (destinationMarker != null) {
                markers.add(destinationMarker)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

