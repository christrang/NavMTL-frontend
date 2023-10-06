package com.example.frontend

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
    private var initialHeight = 200
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
        fun handleTouch(event: MotionEvent) {
            val layoutParams = bottomSheet.layoutParams
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val newHeight = initialHeight - event.rawY.toInt()
                    if (newHeight >= 0) {
                        // Ajustez la hauteur de la vue du bas en fonction du geste
                        layoutParams.height = newHeight
                        bottomSheet.layoutParams = layoutParams
                    }
                }
                MotionEvent.ACTION_UP -> {
                    // Lorsque l'utilisateur relâche, vous pouvez vérifier la hauteur actuelle
                    // Si la hauteur est inférieure à un certain seuil, ajustez-la à la hauteur initiale
                    if (layoutParams.height < initialHeight / 2) {
                        layoutParams.height = initialHeight
                    } else {
                        // Sinon, ajustez la hauteur pour qu'elle prenne tout l'écran
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                    bottomSheet.layoutParams = layoutParams
                }
            }
        }
        bottomSheet = findViewById(R.id.bottom_sheet)
        initialHeight = resources.getDimensionPixelSize(R.dimen.initial_height) // Remplacez R.dimen.initial_height par la ressource appropriée

        bottomSheet.setOnTouchListener { _, event ->
            handleTouch(event)
            true
        }
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
                val address = place.address
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
        val menuButton = findViewById<ImageButton>(R.id.menu_button)
        menuButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        val bottomSheetView = findViewById<LinearLayout>(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.peekHeight = 200
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
        val listePanneau = MutableLiveData<List<RpaData>>()



        // Charger l'image depuis les ressources


        // Redimensionner l'image à la taille souhaitée
        val zoomButton = findViewById<ImageButton>(R.id.zoom_button)

        // Définissez un écouteur de clic pour le bouton
        zoomButton.setOnClickListener {

            // Effectuer le zoom sur Montréal
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(montreal, 17f))
        }
        // Créer un BitmapDescriptor à partir de l'image redimensionnée
        val positionUser = MarkerOptions()
            .position(montreal)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.la_navigation)) // Replace with your origin icon resource
            .title("Moi")
        val positionMarker = googleMap.addMarker(positionUser)
        // Récupérez la liste des panneaux depuis votre API
        repository.main(listePanneau)
        // Observez les changements dans listePanneau
        listePanneau.observe(this, Observer<List<RpaData>> { panneaux ->
            // Réagissez aux changements dans la liste des panneaux
            for (panneau in panneaux) {
                val latitude = panneau.Coordonnees.Latitude
                val longitude = panneau.Coordonnees.Longitude
                val descriptionRpa = panneau.Description_RPA
                val resultatVerification = panneau.Resultat_Verification

                // Utilisez les données comme vous le souhaitez
                // Par exemple, journalisez-les

                // Créez un BitmapDescriptor personnalisé en fonction du résultat de la vérification
                val customMarkerIcon = if (resultatVerification ==="true") {

                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.parking__2_)
                    val width = 40
                    val height = 40
                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
                    BitmapDescriptorFactory.fromBitmap(resizedBitmap)

                } else {
                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.parking__1_)
                    val width = 40
                    val height = 40
                    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
                    BitmapDescriptorFactory.fromBitmap(resizedBitmap)
                }

                // Créez un MarkerOptions avec le nouvel icône personnalisé
                val markerOptions = MarkerOptions()
                    .position(LatLng(latitude, longitude))
                    .icon(customMarkerIcon)
                    .title(descriptionRpa)

                // Ajoutez le marqueur à la carte
                val marker = googleMap.addMarker(markerOptions)

                // Vous pouvez également stocker ces marqueurs dans une liste si nécessaire
                // markerList.add(marker)
            }
        })

    }

    // Handle menu item/button clicks here
    fun handleMenuItemClick(view: android.view.View) {
        when (view.id) {
            R.id.menu_item_1 -> {
                // Handle Menu Item 1 click
                Toast.makeText(this, "Menu Item 1 clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_item_favoris -> {
                // Handle Menu Item 2 click
                Toast.makeText(this, "Menu Item 2 clicked", Toast.LENGTH_SHORT).show()
            }
            // Add more cases for other menu items/buttons as needed
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

