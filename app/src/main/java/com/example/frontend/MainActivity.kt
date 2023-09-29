package com.example.frontend

import android.annotation.SuppressLint
import android.widget.ImageButton

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import androidx.core.view.GravityCompat
import com.google.maps.android.SphericalUtil
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.AutocompleteSupportFragment.*
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val mapFragment = SupportMapFragment.newInstance()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var menuButton: ImageButton
    private lateinit var bottomSheet: LinearLayout
    private var initialHeight = 500
    private lateinit var token: String // Déclarez le token ici
    private lateinit var placesClient: PlacesClient
    private lateinit var autoCompleteFragment: AutocompleteSupportFragment
    private lateinit var editTextAddress: EditText

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

        Places.initialize(applicationContext, "AIzaSyAUcUujvbKP4jVrmo3I00MNI8pdar4Ag0g")
        val placesClient = Places.createClient(this)
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment)
                as AutocompleteSupportFragment
        val montrealLatLngBounds = LatLngBounds(LatLng(45.4215, -73.5696), LatLng(45.6983, -73.4828))
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(montrealLatLngBounds))

// Specify the types of place data to return (in this case, just addresses)
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS))
        editTextAddress = findViewById(R.id.editTextAddress)
// Set up the listener for place selection
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val selectedAddress = place.address
                // Display the selected address in the EditText
                editTextAddress.setText(selectedAddress)
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                val errorMessage = status.statusMessage
                // Handle errors, e.g., show a Toast message
                Toast.makeText(applicationContext, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        })

        // Handle menu button click to open/close the navigation menu
        menuButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)

// Configurez le comportement du BottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
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
        // Set up the map configuration when it's ready
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
        val bottomSheetView = findViewById<LinearLayout>(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)

// Définir le comportement du fond de la feuille
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.peekHeight = 500


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

                        // Vérifier si FLECHE_PAN n'est pas égal à "0" avant d'ajouter la polyline
                        if (panneau.flechePan != "0") {
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
                // Traitez les erreurs ici
                // Par exemple, affichez un message d'erreur à l'utilisateur
                Toast.makeText(this@MainActivity, "Erreur : $errorMessage", Toast.LENGTH_SHORT).show()
                Log.e("Error Panneaux", errorMessage)
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

            // Add more cases for other menu items/buttons as needed
        }
    }
}

