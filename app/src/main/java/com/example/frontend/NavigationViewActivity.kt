package com.example.frontend

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mapbox.api.directions.v5.models.Bearing
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.bindgen.Expected
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.base.TimeFormat
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver
import com.mapbox.navigation.core.lifecycle.requireMapboxNavigation
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.core.replay.ReplayLocationEngine
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver
import com.mapbox.navigation.core.replay.route.ReplayRouteMapper
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver
import com.example.frontend.databinding.ActivityNavigationViewBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.AutocompleteSupportFragment.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.ui.maneuver.view.MapboxManeuverView
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.lifecycle.NavigationBasicGesturesHandler
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.speedlimit.api.MapboxSpeedLimitApi
import com.mapbox.navigation.ui.speedlimit.model.SpeedLimitFormatter
import com.mapbox.navigation.ui.speedlimit.view.MapboxSpeedLimitView
import com.mapbox.navigation.ui.tripprogress.api.MapboxTripProgressApi
import com.mapbox.navigation.ui.tripprogress.model.DistanceRemainingFormatter
import com.mapbox.navigation.ui.tripprogress.model.EstimatedTimeToArrivalFormatter
import com.mapbox.navigation.ui.tripprogress.model.PercentDistanceTraveledFormatter
import com.mapbox.navigation.ui.tripprogress.model.TimeRemainingFormatter
import com.mapbox.navigation.ui.tripprogress.model.TripProgressUpdateFormatter
import com.mapbox.navigation.ui.tripprogress.view.MapboxTripProgressView
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer
import com.mapbox.navigation.ui.voice.model.SpeechAnnouncement
import com.mapbox.navigation.ui.voice.model.SpeechError
import com.mapbox.navigation.ui.voice.model.SpeechValue
import com.mapbox.navigation.ui.voice.model.SpeechVolume
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.location.Geocoder
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.maps.extension.style.expressions.dsl.generated.get
import com.mapbox.maps.extension.style.expressions.dsl.generated.literal
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.eq
import com.mapbox.navigation.ui.maps.building.view.MapboxBuildingView
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.navigation.base.route.RouteAlternativesOptions
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.routealternatives.NavigationRouteAlternativesObserver
import com.mapbox.navigation.core.routealternatives.RouteAlternativesError
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.TimeUnit
import com.mapbox.maps.logE
import com.mapbox.navigation.ui.maps.building.model.BuildingValue
import com.mapbox.navigation.ui.maps.building.model.BuildingError
import android.animation.ValueAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

/**
 * This example demonstrates a basic turn-by-turn navigation experience by putting together some UI elements to showcase
 * navigation camera transitions, guidance instructions banners and playback, and progress along the route.
 *
 * Before running the example make sure you have put your access_token in the correct place
 * inside [app/src/main/res/values/mapbox_access_token.xml]. If not present then add this file
 * at the location mentioned above and add the following content to it
 *
 * <?xml version="1.0" encoding="utf-8"?>
 * <resources xmlns:tools="http://schemas.android.com/tools">
 *     <string name="mapbox_access_token"><PUT_YOUR_ACCESS_TOKEN_HERE></string>
 * </resources>
 *
 * The example assumes that you have granted location permissions and does not enforce it. However,
 * the permission is essential for proper functioning of this example. The example also uses replay
 * location engine to facilitate navigation without actually physically moving.
 *
 * How to use this example:
 * - You can long-click the map to select a destination.
 * - The guidance will start to the selected destination while simulating location updates.
 * You can disable simulation by commenting out the [replayLocationEngine] setter in [NavigationOptions].
 * Then, the device's real location will be used.
 * - At any point in time you can finish guidance or select a new destination.
 * - You can use buttons to mute/unmute voice instructions, recenter the camera, or show the route overview.
 */
@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
class NavigationViewActivity : AppCompatActivity() {

    private companion object {
        private const val BUTTON_ANIMATION_DURATION = 1500L
        const val REQUEST_CODE_LOCATION = 1001
    }
    private lateinit var menuButton: ImageButton
    private lateinit var autoCompleteFragment: AutocompleteSupportFragment
    private lateinit var location : String
    private lateinit var pointLayer: SymbolLayer
    private lateinit var pointSource: GeoJsonSource
    private lateinit var currentPoint: Point
    private lateinit var destin: Point
    private val routeClickPadding = 30 * Resources.getSystem().displayMetrics.density
    private var currentDestinationAnnotation: PointAnnotation? = null
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private var percentDistanceTraveledThreshold: Double = 95.0
    private var distanceRemainingThresholdInMeters = 30
    private var arrivalNotificationHasDisplayed = false
    private val buildingView = MapboxBuildingView()
    private lateinit var AUTH_TOKEN: String
    private val url = "http://navmtl-543ba0ee6069.herokuapp.com/user"
    private lateinit var locStationement: Point
    /**
     * The callback contains a list of buildings returned as a result of querying the [MapboxMap].
     * If no buildings are available, the list is empty
     */
    private val callback =
        MapboxNavigationConsumer<Expected<BuildingError, BuildingValue>> { expected ->
            expected.fold(
                {
                    logE(
                        "ShowBuildingExtrusionsActivity",
                        "error: ${it.errorMessage}"
                    )
                },
                { value ->
                    binding.mapView.getMapboxMap().getStyle { style ->
                        buildingView.highlightBuilding(style, value.buildings)
                    }
                }
            )
        }
            /**
     * Debug tool used to play, pause and seek route progress events that can be used to produce mocked location updates along the route.
     */
    private val mapboxReplayer = MapboxReplayer()


    /**
     * Debug tool that mocks location updates with an input from the [mapboxReplayer].
     */
    private val replayLocationEngine = ReplayLocationEngine(mapboxReplayer)

    /**
     * Debug observer that makes sure the replayer has always an up-to-date information to generate mock updates.
     */
    private val replayProgressObserver = ReplayProgressObserver(mapboxReplayer)

    /**
     * Bindings to the example layout.
     */
    private lateinit var binding: ActivityNavigationViewBinding

    /**
     * Used to execute camera transitions based on the data generated by the [viewportDataSource].
     * This includes transitions from route overview to route following and continuously updating the camera as the location changes.
     */
    private lateinit var navigationCamera: NavigationCamera

    /**
     * Produces the camera frames based on the location and routing data for the [navigationCamera] to execute.
     */
    private lateinit var viewportDataSource: MapboxNavigationViewportDataSource


    /**
     * The data in the [MapboxSpeedLimitView] is formatted by different formatting implementations.
     * Below is the default formatter using default options but you can use your own formatting
     * classes.
     */
    private val speedLimitFormatter: SpeedLimitFormatter by lazy {
        SpeedLimitFormatter(this)
    }

    /**
     * API used for formatting speed limit related data.
     */
    private val speedLimitApi: MapboxSpeedLimitApi by lazy {
        MapboxSpeedLimitApi(speedLimitFormatter)
    }

    /**
     * The SDK triggers [NavigationRouteAlternativesObserver] when available alternatives change.
     */
    private val alternativesObserver = object : NavigationRouteAlternativesObserver {
        override fun onRouteAlternatives(
            routeProgress: RouteProgress,
            alternatives: List<NavigationRoute>,
            routerOrigin: RouterOrigin
        ) {
            // Set the suggested alternatives
            val updatedRoutes = mutableListOf<NavigationRoute>()
            updatedRoutes.add(routeProgress.navigationRoute) // only primary route should persist
            updatedRoutes.addAll(alternatives) // all old alternatives should be replaced by the new ones
            mapboxNavigation.setNavigationRoutes(updatedRoutes)
        }

        override fun onRouteAlternativesError(error: RouteAlternativesError) {
            // no impl
        }
    }



    /*
     * Below are generated camera padding values to ensure that the route fits well on screen while
     * other elements are overlaid on top of the map (including instruction view, buttons, etc.)
     */
    private val pixelDensity = Resources.getSystem().displayMetrics.density
    private val overviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            140.0 * pixelDensity,
            40.0 * pixelDensity,
            120.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val landscapeOverviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            30.0 * pixelDensity,
            380.0 * pixelDensity,
            110.0 * pixelDensity,
            20.0 * pixelDensity
        )
    }
    private val followingPadding: EdgeInsets by lazy {
        EdgeInsets(
            180.0 * pixelDensity,
            40.0 * pixelDensity,
            150.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val landscapeFollowingPadding: EdgeInsets by lazy {
        EdgeInsets(
            30.0 * pixelDensity,
            380.0 * pixelDensity,
            110.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }

    /**
     * Generates updates for the [MapboxManeuverView] to display the upcoming maneuver instructions
     * and remaining distance to the maneuver point.
     */
    private lateinit var maneuverApi: MapboxManeuverApi

    /**
     * Generates updates for the [MapboxTripProgressView] that include remaining time and distance to the destination.
     */
    private lateinit var tripProgressApi: MapboxTripProgressApi

    /**
     * Generates updates for the [routeLineView] with the geometries and properties of the routes that should be drawn on the map.
     */
    private lateinit var routeLineApi: MapboxRouteLineApi

    /**
     * Draws route lines on the map based on the data from the [routeLineApi]
     */
    private lateinit var routeLineView: MapboxRouteLineView

    /**
     * Generates updates for the [routeArrowView] with the geometries and properties of maneuver arrows that should be drawn on the map.
     */
    private val routeArrowApi: MapboxRouteArrowApi = MapboxRouteArrowApi()

    /**
     * Draws maneuver arrows on the map based on the data [routeArrowApi].
     */
    private lateinit var routeArrowView: MapboxRouteArrowView

    /**
     * Stores and updates the state of whether the voice instructions should be played as they come or muted.
     */
    private var isVoiceInstructionsMuted = false
        set(value) {
            field = value
            if (value) {
                binding.soundButton.muteAndExtend(BUTTON_ANIMATION_DURATION)
                voiceInstructionsPlayer.volume(SpeechVolume(0f))
            } else {
                binding.soundButton.unmuteAndExtend(BUTTON_ANIMATION_DURATION)
                voiceInstructionsPlayer.volume(SpeechVolume(1f))
            }
        }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /**
     * Extracts message that should be communicated to the driver about the upcoming maneuver.
     * When possible, downloads a synthesized audio file that can be played back to the driver.
     */
    private lateinit var speechApi: MapboxSpeechApi

    /**
     * Plays the synthesized audio files with upcoming maneuver instructions
     * or uses an on-device Text-To-Speech engine to communicate the message to the driver.
     * NOTE: do not use lazy initialization for this class since it takes some time to initialize
     * the system services required for on-device speech synthesis. With lazy initialization
     * there is a high risk that said services will not be available when the first instruction
     * has to be played. [MapboxVoiceInstructionsPlayer] should be instantiated in
     * `Activity#onCreate`.
     */
    private lateinit var voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer

    /**
     * Observes when a new voice instruction should be played.
     */
    private val voiceInstructionsObserver = VoiceInstructionsObserver { voiceInstructions ->
        speechApi.generate(voiceInstructions, speechCallback)
    }

    /**
     * Based on whether the synthesized audio file is available, the callback plays the file
     * or uses the fall back which is played back using the on-device Text-To-Speech engine.
     */
    private val speechCallback =
        MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> { expected ->
            expected.fold(
                { error ->
                    // play the instruction via fallback text-to-speech engine
                    voiceInstructionsPlayer.play(
                        error.fallback,
                        voiceInstructionsPlayerCallback
                    )
                },
                { value ->
                    // play the sound file from the external generator
                    voiceInstructionsPlayer.play(
                        value.announcement,
                        voiceInstructionsPlayerCallback
                    )
                }
            )
        }

    /**
     * When a synthesized audio file was downloaded, this callback cleans up the disk after it was played.
     */
    private val voiceInstructionsPlayerCallback =
        MapboxNavigationConsumer<SpeechAnnouncement> { value ->
            // remove already consumed file to free-up space
            speechApi.clean(value)
        }

    /**
     * [NavigationLocationProvider] is a utility class that helps to provide location updates generated by the Navigation SDK
     * to the Maps SDK in order to update the user location indicator on the map.
     */
    private val navigationLocationProvider = NavigationLocationProvider()

    /**
     * Gets notified with location updates.
     *
     * Exposes raw updates coming directly from the location services
     * and the updates enhanced by the Navigation SDK (cleaned up and matched to the road).
     */
    private val locationObserver = object : LocationObserver {
        var firstLocationUpdateReceived = false

        override fun onNewRawLocation(rawLocation: Location) {
            // not handled
        }

        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            val enhancedLocation = locationMatcherResult.enhancedLocation
            // update location puck's position on the map
            navigationLocationProvider.changePosition(

                location = enhancedLocation,
                keyPoints = locationMatcherResult.keyPoints,
            )
            location = enhancedLocation.toString()
            // update camera position to account for new location
            viewportDataSource.onLocationChanged(enhancedLocation)
            viewportDataSource.evaluate()

            // if this is the first location update the activity has received,
            // it's best to immediately move the camera to the current user location
            if (!firstLocationUpdateReceived) {
                firstLocationUpdateReceived = true
                navigationCamera.requestNavigationCameraToOverview(
                    stateTransitionOptions = NavigationCameraTransitionOptions.Builder()
                        .maxDuration(0) // instant transition
                        .build()
                )
            }
        }
    }

    /**
     * Gets notified with progress along the currently active route.
     */
    fun formatTime(totalSeconds: Long): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60

        return if (hours > 0) {
            String.format("%02d:%02d", hours, minutes)
        } else {
            String.format("%02d min", minutes)
        }
    }


    private val routeProgressObserver = RouteProgressObserver { routeProgress ->

        val totalDistance = routeProgress.distanceTraveled + routeProgress.distanceRemaining
        val percentDistanceTraveled = (routeProgress.distanceTraveled / totalDistance) * 100
        if (
            percentDistanceTraveled >= percentDistanceTraveledThreshold &&
            routeProgress.distanceRemaining <= distanceRemainingThresholdInMeters &&
            !arrivalNotificationHasDisplayed
        ) {
            arrivalNotificationHasDisplayed = true

        }
        val arrivalCardView = findViewById<CardView>(R.id.arrivalCardView)
        val buttonYes = findViewById<ImageButton>(R.id.buttonYes)
        val buttonNo = findViewById<ImageButton>(R.id.buttonNo)
        val station = findViewById<ImageButton>(R.id.station)
        val arrive = findViewById<CardView>(R.id.arrivalParkingCardView)
        // Imaginons que cette fonction est appelée lorsque l'utilisateur arrive à destination
        station.setOnClickListener {
        arrivalCardView.visibility = View.VISIBLE

        // Configurer les gestionnaires de clics pour les boutons
        buttonYes.setOnClickListener {
            // Gérer le clic sur 'Oui'
            // Par exemple, enregistrer l'état du stationnement
            Toast.makeText(this, "Oui!", Toast.LENGTH_LONG).show()
            arrivalCardView.visibility = View.GONE
           locStationement = currentPoint
            val repository = Repository(application)
            Log.d("destin", locStationement.toString())
            val listePanneau = MutableLiveData<List<RpaData>>()
            repository.main(listePanneau, locStationement.coordinates()[1], locStationement.coordinates()[0])

            arrive.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                // This code will be executed after 5 seconds
                clearRouteAndStopNavigation()
                arrive.visibility = View.GONE // Hide the card view
            }, 5000)
            val progressBar = findViewById<ProgressBar>(R.id.countdownProgressBar)
            val animation = ValueAnimator.ofInt(100, 0)
            animation.duration = 15000 // Duration of 5 seconds
            animation.addUpdateListener { animation ->
                progressBar.progress = animation.animatedValue as Int
            }
            animation.start()


        }

        buttonNo.setOnClickListener {
            // Gérer le clic sur 'Non'
            // Par exemple, proposer d'autres options de stationnement
            Toast.makeText(this, "Non!", Toast.LENGTH_LONG).show()
            arrivalCardView.visibility = View.GONE
        }
        }
        // update the camera position to account for the progressed fragment of the route
        val tripProgress = tripProgressApi.getTripProgress(routeProgress)
        val totalTimeRemainingFormatted = formatTime(tripProgress.totalTimeRemaining.toLong())
        binding.tempsRestant.text = totalTimeRemainingFormatted

        // Format and set the distance remaining
        val distanceRemainingKm = tripProgress.distanceRemaining / 1000
        val distanceText = String.format("%.2f km", distanceRemainingKm)
        binding.distanceREstante.text = distanceText

        // Format and set the estimated time to arrival
        val estimatedArrivalTime = Date(tripProgress.estimatedTimeToArrival)
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedTime = dateFormat.format(estimatedArrivalTime)
        binding.tempsEstime.text = formattedTime

        binding.tripProgressView.render(tripProgress)
        viewportDataSource.onRouteProgressChanged(routeProgress)
        viewportDataSource.evaluate()

        // draw the upcoming maneuver arrow on the map
        val style = binding.mapView.getMapboxMap().getStyle()
        if (style != null) {
            val maneuverArrowResult = routeArrowApi.addUpcomingManeuverArrow(routeProgress)
            routeArrowView.renderManeuverUpdate(style, maneuverArrowResult)
        }

        // update top banner with maneuver instructions
        val maneuvers = maneuverApi.getManeuvers(routeProgress)
        maneuvers.fold(
            { error ->
                Toast.makeText(
                    this@NavigationViewActivity,
                    error.errorMessage,
                    Toast.LENGTH_SHORT
                ).show()
            },
            {
                binding.maneuverView.visibility = View.VISIBLE
                binding.maneuverView.renderManeuvers(maneuvers)
            }
        )

        // update bottom trip progress summary
        binding.tripProgressView.render(
            tripProgressApi.getTripProgress(routeProgress)
        )
    }

    /**
     * Gets notified whenever the tracked routes change.
     *
     * A change can mean:
     * - routes get changed with [MapboxNavigation.setRoutes]
     * - routes annotations get refreshed (for example, congestion annotation that indicate the live traffic along the route)
     * - driver got off route and a reroute was executed
     */
    private val routesObserver = RoutesObserver { routeUpdateResult ->
        if (routeUpdateResult.navigationRoutes.isNotEmpty()) {
            // generate route geometries asynchronously and render them
            routeLineApi.setNavigationRoutes(
                routeUpdateResult.navigationRoutes,
                alternativeRoutesMetadata = mapboxNavigation.getAlternativeMetadataFor(
                    routeUpdateResult.navigationRoutes
                )
            ) { value ->
                binding.mapView.getMapboxMap().getStyle()?.apply {
                    routeLineView.renderRouteDrawData(this, value)
                }
            }

            // update the camera position to account for the new route
            viewportDataSource.onRouteChanged(routeUpdateResult.navigationRoutes.first())
            viewportDataSource.evaluate()
        } else {
            // remove the route line and route arrow from the map
            val style = binding.mapView.getMapboxMap().getStyle()
            if (style != null) {
                routeLineApi.clearRouteLine { value ->
                    routeLineView.renderClearRouteLineValue(
                        style,
                        value
                    )
                }
                routeArrowView.render(style, routeArrowApi.clearArrows())
            }

            // remove the route reference from camera position evaluations
            viewportDataSource.clearRouteData()
            viewportDataSource.evaluate()
        }
    }

    private val mapClickListener = OnMapClickListener { point ->
        lifecycleScope.launch {
            routeLineApi.findClosestRoute(
                point,
                binding.mapView.getMapboxMap(),
                routeClickPadding
            ) {
                val routeFound = it.value?.navigationRoute
                // if we clicked on some route that is not primary,
                // we make this route primary and all the others - alternative
                if (routeFound != null && routeFound != routeLineApi.getPrimaryNavigationRoute()) {
                    val reOrderedRoutes = routeLineApi.getNavigationRoutes()
                        .filter { navigationRoute -> navigationRoute != routeFound }
                        .toMutableList()
                        .also { list ->
                            list.add(0, routeFound)
                        }
                        previewRoutes(reOrderedRoutes)
                }
            }
        }
        false
    }

    private val mapboxNavigation: MapboxNavigation by requireMapboxNavigation(
        onResumedObserver = object : MapboxNavigationObserver {
            @SuppressLint("MissingPermission")
            override fun onAttached(mapboxNavigation: MapboxNavigation) {
                mapboxNavigation.registerRoutesObserver(routesObserver)
                mapboxNavigation.registerLocationObserver(locationObserver)
                mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
                mapboxNavigation.registerRouteProgressObserver(replayProgressObserver)
                mapboxNavigation.registerVoiceInstructionsObserver(voiceInstructionsObserver)
                mapboxNavigation.registerRouteAlternativesObserver(alternativesObserver)
                // start the trip session to being receiving location updates in free drive
                // and later when a route is set also receiving route progress updates
                mapboxNavigation.startTripSession()
            }

            override fun onDetached(mapboxNavigation: MapboxNavigation) {
                mapboxNavigation.unregisterRoutesObserver(routesObserver)
                mapboxNavigation.unregisterLocationObserver(locationObserver)
                mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
                mapboxNavigation.unregisterRouteProgressObserver(replayProgressObserver)
                mapboxNavigation.unregisterVoiceInstructionsObserver(voiceInstructionsObserver)
                mapboxNavigation.registerRouteAlternativesObserver(alternativesObserver)
            }
        },
        onInitialize = this::initNavigation,
    )

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_view)



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding = ActivityNavigationViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pointAnnotationManager = binding.mapView.annotations.createPointAnnotationManager()
        binding.mapView.gestures.addOnMapClickListener(mapClickListener)
        // Récupérez la liste des panneaux depuis votre API
        val repository = Repository(application)






        binding.placeAutocompleteButton.setOnClickListener {
            val intent = Intent(this, PlaceActivity::class.java).apply {
                putExtra("Location", location)

            }
            startActivity(intent)
        }

        menuButton = findViewById(R.id.menu_button)
        // initialize Navigation Camera
        viewportDataSource = MapboxNavigationViewportDataSource(binding.mapView.getMapboxMap())
        navigationCamera = NavigationCamera(
            binding.mapView.getMapboxMap(),
            binding.mapView.camera,
            viewportDataSource
        )
        // set the animations lifecycle listener to ensure the NavigationCamera stops
        // automatically following the user location when the map is interacted with
        binding.mapView.camera.addCameraAnimationsLifecycleListener(
            NavigationBasicGesturesHandler(navigationCamera)
        )
        navigationCamera.registerNavigationCameraStateChangeObserver { navigationCameraState ->
            // shows/hide the recenter button depending on the camera state
            when (navigationCameraState) {
                NavigationCameraState.TRANSITION_TO_FOLLOWING,
                NavigationCameraState.FOLLOWING -> binding.recenter.visibility = View.INVISIBLE
                NavigationCameraState.TRANSITION_TO_OVERVIEW,
                NavigationCameraState.OVERVIEW,
                NavigationCameraState.IDLE -> binding.recenter.visibility = View.VISIBLE
            }
        }
        // set the padding values depending on screen orientation and visible view layout
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewportDataSource.overviewPadding = landscapeOverviewPadding
        } else {
            viewportDataSource.overviewPadding = overviewPadding
        }
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewportDataSource.followingPadding = landscapeFollowingPadding
        } else {
            viewportDataSource.followingPadding = followingPadding
        }
        val menuButton = findViewById<ImageButton>(R.id.menu_button)
        menuButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        // make sure to use the same DistanceFormatterOptions across different features
        val distanceFormatterOptions = DistanceFormatterOptions.Builder(this).build()

        // initialize maneuver api that feeds the data to the top banner maneuver view
        maneuverApi = MapboxManeuverApi(
            MapboxDistanceFormatter(distanceFormatterOptions)
        )

        // initialize bottom progress view
        tripProgressApi = MapboxTripProgressApi(
            TripProgressUpdateFormatter.Builder(this)
                .distanceRemainingFormatter(
                    DistanceRemainingFormatter(distanceFormatterOptions)
                )
                .timeRemainingFormatter(
                    TimeRemainingFormatter(this)
                )
                .percentRouteTraveledFormatter(
                    PercentDistanceTraveledFormatter()
                )
                .estimatedTimeToArrivalFormatter(
                    EstimatedTimeToArrivalFormatter(this, TimeFormat.TWENTY_FOUR_HOURS)
                )
                .build()
        )

        // initialize voice instructions api and the voice instruction player
        speechApi = MapboxSpeechApi(
            this,
            getString(R.string.mapbox_access_token),
            Locale.CANADA_FRENCH.language
        )
        voiceInstructionsPlayer = MapboxVoiceInstructionsPlayer(
            this,
            getString(R.string.mapbox_access_token),
            Locale.CANADA_FRENCH.language
        )

        // initialize route line, the withRouteLineBelowLayerId is specified to place
        // the route line below road labels layer on the map
        // the value of this option will depend on the style that you are using
        // and under which layer the route line should be placed on the map layers stack
        val mapboxRouteLineOptions = MapboxRouteLineOptions.Builder(this)
            .withRouteLineBelowLayerId("road-label-navigation")
            .build()
        routeLineApi = MapboxRouteLineApi(mapboxRouteLineOptions)
        routeLineView = MapboxRouteLineView(mapboxRouteLineOptions)

        // initialize maneuver arrow view to draw arrows on the map
        val routeArrowOptions = RouteArrowOptions.Builder(this).build()
        routeArrowView = MapboxRouteArrowView(routeArrowOptions)

        // load map style
        binding.mapView.getMapboxMap().loadStyleUri("mapbox://styles/marvenschery/clnzg3ia300aa01qs6spv0mt7") { style ->

            // Ajouter vos images
           style.addImage(
                "mon_icone",
                BitmapFactory.decodeResource(resources, R.drawable.parking__3_)
           )

           // Ajouter une source GeoJson
            val source = geoJsonSource("source_id") {
                featureCollection(FeatureCollection.fromFeatures(emptyList()))
            }
           style.addSource(source)

            // Ajouter un layer
            val layer = symbolLayer("layer_id", "source_id") {
                iconImage("mon_icone")
                iconAllowOverlap(true)
                iconAnchor(IconAnchor.BOTTOM)
            }
            style.addLayer(layer)



            // Ajouter un écouteur de clic long sur la carte
            binding.mapView.gestures.addOnMapLongClickListener { point ->
                val bottomSheetLayout: LinearLayout = findViewById(R.id.bottom_sheet)
                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                val latitude = point.latitude()
                val longitude = point.longitude()

                infoPanel(latitude, longitude)

                true
            }

            // Gérer les données provenant de l'intent
            intent?.let {
                val address = it.getStringExtra("selectedAddress")
                val latitude1 = it.getDoubleExtra("latitude", 0.0)
                val longitude1 = it.getDoubleExtra("longitude", 0.0)

                Handler(Looper.getMainLooper()).postDelayed({
                    if(latitude1 !=  0.0) {
                        infoPanel(latitude1, longitude1)
                    }

                }, 1)
            }
            val loadingLayout = findViewById<CardView>(R.id.loadingCardView)

            // Afficher le ProgressBar
            loadingLayout.visibility = View.VISIBLE

            val listePanneau = MutableLiveData<List<RpaData>>()
            Log.d("dvfv","decd")
            replayOriginLocation()

            // Assuming currentPoint is a nullable type (e.g., Location?)
            if (currentPoint != null) {
                Log.d("on","a")
                // Check if currentPoint has valid coordinates
                val latitude = currentPoint.coordinates()[1]
                val longitude = currentPoint.coordinates()[0]

                if (latitude != null && longitude != null) {
                    repository.main(listePanneau, latitude, longitude)
                } else {
                    Log.d("on","a pas cordi")
                }
            } else {
                Log.d("on","a pas")
            }

            listePanneau.observe(this, Observer { panneauxList ->
                Log.d("panneau", panneauxList.toString())
                updateLinesOnMap(panneauxList,5.0)
                updatePointsOnMap(panneauxList)

                // Masquer le ProgressBar après le chargement des données
                loadingLayout.visibility = View.GONE
            })
        }


        // initialize view interactions
        binding.stop.setOnClickListener {
            clearRouteAndStopNavigation()
            binding.arrivalCardView.visibility = View.INVISIBLE
            binding.infoPanel.visibility = View.INVISIBLE
            val bottomSheetLayout: LinearLayout = findViewById(R.id.bottom_sheet)
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
            bottomSheetBehavior.peekHeight = 400
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.recenter.setOnClickListener {
            navigationCamera.requestNavigationCameraToFollowing()
            binding.routeOverview.showTextAndExtend(BUTTON_ANIMATION_DURATION)
        }
        binding.routeOverview.setOnClickListener {
            navigationCamera.requestNavigationCameraToOverview()
            binding.recenter.showTextAndExtend(BUTTON_ANIMATION_DURATION)
        }
        binding.soundButton.setOnClickListener {
            // mute/unmute voice instructions
            isVoiceInstructionsMuted = !isVoiceInstructionsMuted
        }

        // set initial sounds button state
        binding.soundButton.unmute()
        val bottomSheetLayout: LinearLayout = findViewById(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior.peekHeight = 450
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        bottomSheetBehavior.peekHeight = 500
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        // Handle the settling state
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // Handle the expanded state
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        // Handle the hidden state
                    }
                    else -> {
                        // Handle the default state
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Handle the sliding state
            }
        })


    }
    fun addToSearchHistory(address: String, latitude: String, longitude: String) {
        val addToHistoryTask = AddToHistoryTask(this)
        addToHistoryTask.execute(address, latitude, longitude)
    }

    private fun addFavorite() {
        val buttonAddFavoris = findViewById<ImageButton>(R.id.AddFavorite)
        val addressText = findViewById<TextView>(R.id.addressText)

        buttonAddFavoris.setOnClickListener {
            val address = addressText.text.toString()

            // Check if the address is not empty before adding it to the favorites
            if (address.isNotEmpty()) {
                // Call the function to add the address to the favorites
                addToFavorites(address)
            }
        }
    }

    private fun addToFavorites(address: String) {
        val favoriViewModel = ViewModelProvider(this).get(FavoriViewModel::class.java)
        favoriViewModel.addToFavorites(address)
    }
    private fun createLineForPanneau(panneau: RpaData, lineLength: Double): Feature {
        // Exemple simple où on prend la direction vers le nord pour la démonstration
        val startLatitude = panneau.Coordonnees.Latitude
        val startLongitude = panneau.Coordonnees.Longitude

        // Calcul simple pour une ligne vers le nord, à ajuster en fonction de la direction réelle
        val endLatitude = startLatitude + (lineLength / 111974) // approximativement 1 degré de latitude = 111 km

        val startPoint = Point.fromLngLat(startLongitude, startLatitude)
        val endPoint = Point.fromLngLat(startLongitude, endLatitude)

        return Feature.fromGeometry(LineString.fromLngLats(listOf(startPoint, endPoint)))
    }

    private fun updateLinesOnMap(panneauxList: List<RpaData>, lineLength: Double) {
        val features = panneauxList.map { panneau ->
            createLineForPanneau(panneau, lineLength)
        }
        val featureCollection = FeatureCollection.fromFeatures(features)

        val truePanneauxList = panneauxList.filter { it.Resultat_Verification == "true" }
        val falsePanneauxList = panneauxList.filter { it.Resultat_Verification != "true" }

// Generating features
        val trueFeatures = truePanneauxList.map { createLineForPanneau(it, lineLength) }
        val falseFeatures = falsePanneauxList.map { createLineForPanneau(it, lineLength) }

        binding.mapView.getMapboxMap().getStyle { style ->
            // Identifiers for the "true" features
            val trueSourceId = "true-line-source"
            val trueLayerId = "true-line-layer"

            // First remove the layer that uses the source
            if (style.styleLayerExists(trueLayerId)) {
                style.removeStyleLayer(trueLayerId)
            }

            // Then remove the source
            if (style.styleSourceExists(trueSourceId)) {
                style.removeStyleSource(trueSourceId)
            }

            // Now it's safe to add the source back
            val trueSource = geoJsonSource(trueSourceId) {
                featureCollection(FeatureCollection.fromFeatures(trueFeatures))
            }
            style.addSource(trueSource)

            // Re-add the layer
            val trueLineLayer = lineLayer(trueLayerId, trueSourceId) {
                lineColor("red")
                lineWidth(10.0)
            }
            style.addLayer(trueLineLayer)

            // Repeat the process for the "false" features
            val falseSourceId = "false-line-source"
            val falseLayerId = "false-line-layer"

            // Remove the layer
            if (style.styleLayerExists(falseLayerId)) {
                style.removeStyleLayer(falseLayerId)
            }

            // Remove the source
            if (style.styleSourceExists(falseSourceId)) {
                style.removeStyleSource(falseSourceId)
            }

            // Add the source back
            val falseSource = geoJsonSource(falseSourceId) {
                featureCollection(FeatureCollection.fromFeatures(falseFeatures))
            }
            style.addSource(falseSource)

            // Re-add the layer
            val falseLineLayer = lineLayer(falseLayerId, falseSourceId) {
                lineColor("blue")
                lineWidth(10.0)
            }
            style.addLayer(falseLineLayer)
        }
    }
    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) as VectorDrawable?
        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
    private fun updatePointsOnMap(panneauxList: List<RpaData>) {
        // Séparer les données en deux listes selon 'Resultat_Verification'
        val trueFeatures = panneauxList.filter { it.Resultat_Verification == "true" }
            .map { rpaData ->
                Feature.fromGeometry(Point.fromLngLat(rpaData.Coordonnees.Longitude, rpaData.Coordonnees.Latitude)).apply {
                    addStringProperty("icon", "true_icon") // Ajouter une propriété personnalisée pour identifier l'image à utiliser
                }
            }
        val falseFeatures = panneauxList.filter { it.Resultat_Verification == "false" }
            .map { rpaData ->
                Feature.fromGeometry(Point.fromLngLat(rpaData.Coordonnees.Longitude, rpaData.Coordonnees.Latitude)).apply {
                    addStringProperty("icon", "false_icon") // Ajouter une propriété personnalisée pour identifier l'image à utiliser
                }
            }

        val featureCollection = FeatureCollection.fromFeatures(trueFeatures + falseFeatures)

        binding.mapView.getMapboxMap().loadStyleUri("mapbox://styles/marvenschery/clnzg3ia300aa01qs6spv0mt7") { style ->
            val trueIconBitmap = getBitmapFromVectorDrawable(this, R.drawable.parking_true)
            val falseIconBitmap = getBitmapFromVectorDrawable(this, R.drawable.parking_false)

            if (trueIconBitmap != null) {
                style.addImage("true_icon", trueIconBitmap)
            }
            if (falseIconBitmap != null) {
                style.addImage("false_icon", falseIconBitmap)
            }

            // Ajouter une source GeoJson
            val source = geoJsonSource("source_id") {
                featureCollection(featureCollection)
            }
            style.addSource(source)

            // Ajouter un layer pour 'true'
            val trueLayer = symbolLayer("true_layer_id", "source_id") {
                iconImage("{icon}")
                iconAllowOverlap(true)
                iconAnchor(IconAnchor.BOTTOM)
                filter(eq(get("icon"), literal("true_icon")))
            }
            style.addLayer(trueLayer)

            // Ajouter un layer pour 'false'
            val falseLayer = symbolLayer("false_layer_id", "source_id") {
                iconImage("{icon}")
                iconAllowOverlap(true)
                iconAnchor(IconAnchor.BOTTOM)
                filter(eq(get("icon"), literal("false_icon")))
            }
            style.addLayer(falseLayer)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        mapboxReplayer.finish()
        maneuverApi.cancel()
        routeLineApi.cancel()
        routeLineView.cancel()
        speechApi.cancel()
        voiceInstructionsPlayer.shutdown()

    }

    private fun initNavigation() {
        val bottomSheetLinearLayout: LinearLayout = findViewById(R.id.bottom_sheet)
        val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> = BottomSheetBehavior.from(bottomSheetLinearLayout)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        Log.e("location", "bootom")
        MapboxNavigationApp.setup(
            NavigationOptions.Builder(this)
                .accessToken(getString(R.string.mapbox_access_token))
                // comment out the location engine setting block to disable simulation
                .locationEngine(replayLocationEngine)
                .routeAlternativesOptions(
                    RouteAlternativesOptions.Builder()
                        .intervalMillis(TimeUnit.MINUTES.toMillis(3))
                        .build()
                )
                .build()
        )

        // initialize location puck
        binding.mapView.location.apply {
            setLocationProvider(navigationLocationProvider)
            this.locationPuck = LocationPuck2D(
                bearingImage = ContextCompat.getDrawable(
                    this@NavigationViewActivity,
                    R.drawable.navigationicon
                )
            )
            enabled = true
        }
        Log.e("location", "ici")
        navigationCamera.requestNavigationCameraToFollowing()
        replayOriginLocation()
    }

    private fun replayOriginLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {


            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    Log.e("Helo", "Yes Permis")
                     currentPoint = Point.fromLngLat(it.longitude, it.latitude)
                    destin = currentPoint


                    mapboxReplayer.pushEvents(
                        listOf(
                            ReplayRouteMapper.mapToUpdateLocation(
                                Date().time.toDouble(),
                                currentPoint
                            )
                        )
                    )
                    mapboxReplayer.playFirstLocation()
                    mapboxReplayer.playbackSpeed(3.0)
                }
            }
        } else {

            REQUEST_CODE_LOCATION
            Manifest.permission.ACCESS_FINE_LOCATION
            Log.e("permission", "no permission")
        }
    }

    private fun findRoute(destination: Point) {
        val bottomSheetLinearLayout: LinearLayout = findViewById(R.id.bottom_sheet)
        val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> = BottomSheetBehavior.from(bottomSheetLinearLayout)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        val originLocation = navigationLocationProvider.lastLocation
        val originPoint = originLocation?.let {
            Point.fromLngLat(it.longitude, it.latitude)
        } ?: return
            Log.e("destination", destination.toString())
            destin = destination
        val loadingLayout = findViewById<CardView>(R.id.loadingCardView)
        loadingLayout.visibility = View.VISIBLE
        val repository = Repository(application)
        Log.d("destin", destin.toString())
        val listePanneau = MutableLiveData<List<RpaData>>()
        repository.main(listePanneau, destin.coordinates()[1], destin.coordinates()[0])


        listePanneau.observe(this, Observer { panneauxList ->
            Log.d("panneau", panneauxList.toString())
            updateLinesOnMap(panneauxList,5.0)
            updatePointsOnMap(panneauxList)

            loadingLayout.visibility = View.GONE
        })
        // execute a route request
        // it's recommended to use the
        // applyDefaultNavigationOptions and applyLanguageAndVoiceUnitOptions
        // that make sure the route request is optimized
        // to allow for support of all of the Navigation SDK features
        mapboxNavigation.requestRoutes(
            RouteOptions.builder()
                .applyDefaultNavigationOptions()
                .applyLanguageAndVoiceUnitOptions(this)
                .coordinatesList(listOf(originPoint, destination))
                .alternatives(true)
                // provide the bearing for the origin of the request to ensure
                // that the returned route faces in the direction of the current user movement
                .bearingsList(
                    listOf(
                        Bearing.builder()
                            .angle(originLocation.bearing.toDouble())
                            .degrees(45.0)
                            .build(),
                        null
                    )
                )
                .layersList(listOf(mapboxNavigation.getZLevel(), null))
                .build(),
            object : NavigationRouterCallback {
                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) {
                    // no impl
                }

                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {

                }

                override fun onRoutesReady(
                    routes: List<NavigationRoute>,
                    routerOrigin: RouterOrigin
                ) {
                    previewRoutes(routes)

                    addDestinationMarker(destination)
                }
            }
        )
    }
    private fun addDestinationMarker(destination: Point) {
        val context = this

        binding.mapView.getMapboxMap().getStyle { style ->
            // Supprimer l'ancien marqueur
            currentDestinationAnnotation?.let { pointAnnotationManager.delete(it) }

            // Convertir l'image vectorielle en Bitmap
            val destinationIconBitmap = getBitmapFromVectorDrawable(context, R.drawable.marker)
            if (destinationIconBitmap != null) {
                style.addImage("destination_icon", destinationIconBitmap)
            }

            // Créer une PointAnnotationOptions pour le nouveau marqueur
            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(destination)
                .withIconImage("destination_icon")

            // Ajouter l'annotation et mettre à jour la référence
            currentDestinationAnnotation = pointAnnotationManager.create(pointAnnotationOptions)
        }
    }
    private fun previewRoutes(routes: List<NavigationRoute>) {
        // Mapbox navigation doesn't have a special state for route preview.
        // Preview state is managed by an application.
        // Display the routes you received on the map.
        routeLineApi.setNavigationRoutes(routes) { value ->
            binding.mapView.getMapboxMap().getStyle().apply {
                this?.let { routeLineView.renderRouteDrawData(it, value) }
                // update the camera position to account for the new route
                viewportDataSource.onRouteChanged(routes.first())
                viewportDataSource.evaluate()
                navigationCamera.requestNavigationCameraToOverview()
            }
        }
        binding.buttonOverview.setOnClickListener {
            navigationCamera.requestNavigationCameraToOverview()
        }
        binding.buttonGo.setOnClickListener {
            binding.infoPanel.visibility = View.INVISIBLE
            setRouteAndStartNavigation(routes)
        }
    }

    private fun setRouteAndStartNavigation(routes: List<NavigationRoute>) {
        binding.routeOverview.showTextAndExtend(BUTTON_ANIMATION_DURATION)
        // set routes, where the first route in the list is the primary route that
        // will be used for active guidance
        val bottomSheetLinearLayout: LinearLayout = findViewById(R.id.bottom_sheet)
        val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> = BottomSheetBehavior.from(bottomSheetLinearLayout)
        bottomSheetBehavior.peekHeight = 0
        mapboxNavigation.setNavigationRoutes(routes)

        // show UI elements
        binding.soundButton.visibility = View.VISIBLE
        binding.routeOverview.visibility = View.VISIBLE
        binding.tripProgressCard.visibility = View.VISIBLE
        // move the camera to overview when new route is available
        navigationCamera.requestNavigationCameraToOverview()
    }
    private fun infoPanel(lat: Double, long: Double) {
       /* binding.mapView.gestures.addOnMapClickListener { point ->
            val bottomSheetLayout: LinearLayout = findViewById(R.id.bottom_sheet)
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            val latitude = point.latitude()
            val longitude = point.longitude()

            binding.infoPanel.visibility = View.INVISIBLE

            false
        }*/
        binding.infoPanel.visibility = View.VISIBLE
        binding.tripProgressCard.visibility = View.INVISIBLE
        // Initialisation du BottomSheetBehavior
        val bottomSheetLinearLayout: LinearLayout = findViewById(R.id.bottom_sheet)
        val bottomSheetBehavior: BottomSheetBehavior<LinearLayout> = BottomSheetBehavior.from(bottomSheetLinearLayout)
        bottomSheetBehavior.peekHeight = 0

        // Mise à jour des coordonnées dans l'interface utilisateur
        val addressTextView: TextView = findViewById(R.id.addressText)
        addressTextView.text = "Lat: $lat, Long: $long"

        // Utilisation de l'API Geocoding pour convertir les coordonnées en adresse
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(lat, long, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses?.get(0)
                    // Affichez l'adresse complète ou formatez-la comme vous le souhaitez
                    if (address != null) {
                        addressTextView.text = address.getAddressLine(0)
                    }
                } else {
                    addressTextView.text = "Adresse non trouvée"
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            addressTextView.text = "Erreur lors de la recherche de l'adresse"
        }

        // Utilisation du point pour trouver un itinéraire
        val point = Point.fromLngLat(long, lat)
        findRoute(point)
    }

    private fun clearRouteAndStopNavigation() {
        val bottomSheetLayout: LinearLayout = findViewById(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior.peekHeight = 400
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        // clear
        mapboxNavigation.setNavigationRoutes(listOf())

        // stop simulation
        mapboxReplayer.stop()

        // hide UI elements
        binding.soundButton.visibility = View.INVISIBLE
        binding.maneuverView.visibility = View.INVISIBLE
        binding.routeOverview.visibility = View.INVISIBLE
        binding.tripProgressCard.visibility = View.INVISIBLE
    }
    private fun getProfile() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $AUTH_TOKEN")
                    .build()

                val response = client.newCall(request).execute()
                val responseData = response.body?.string()

                withContext(Dispatchers.Main) {
                    if (responseData != null) {
                        // Parse the JSON response and update UI elements
                        // Replace with the actual parsing logic
                        val jsonObject = JSONObject(responseData)
                        val nom = jsonObject.optString("prenom")

                        // Update UI elements with the fetched data
                        val lastNameTextView = findViewById<TextView>(R.id.nom1)
                        lastNameTextView.visibility = View.VISIBLE
                        val bonjour = ("Salut, $nom")

                        lastNameTextView.text = bonjour

                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
