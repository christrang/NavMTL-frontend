package com.example.frontend

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlaceActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var listView: ListView
    private lateinit var service: GoogleMapsService
    private lateinit var adapter: PlaceAdapter
    private lateinit var location: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        editText = findViewById(R.id.editText1)
        listView = findViewById(R.id.listView)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(GoogleMapsService::class.java)
        val places = mutableListOf<PlaceItem>() // Liste pour stocker les objets PlaceItem
        adapter = PlaceAdapter(this, places)
        listView.adapter = adapter


        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Not used in this example
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used in this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length ?: 0 > 0) {
                    // Change icon to 'close' when user starts typing
                    editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.close_svgrepo_com, 0, 0, 0)
                } else {
                    // Change icon back to 'back' when EditText is empty
                    editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back_svgrepo_com, 0, 0, 0)
                }
            }
        })

// Add click listener for the 'close' icon to clear the text
        editText.setOnTouchListener(View.OnTouchListener { v, event ->
            val drawableLeft = editText.compoundDrawables[0]
            val touchThreshold1 = 50
            if (event.action == MotionEvent.ACTION_UP) {
                // If the icon is the "close" icon
                if (editText.text.isNotEmpty() && drawableLeft != null && event.rawX <= (editText.right + drawableLeft.bounds.width() )) {
                    // User clicked on the 'close' icon
                    editText.setText("")
                    return@OnTouchListener true
                }
                // If the icon is the "back" icon
                else if (editText.text.isEmpty() && drawableLeft != null && event.rawX <= (editText.left + drawableLeft.bounds.width() + touchThreshold1)) {
                    // User clicked on the 'back' icon
                    val intent = Intent(this@PlaceActivity, NavigationViewActivity::class.java)
                    startActivity(intent)
                    return@OnTouchListener true
                }
            }
            false
        })







        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                // Extracting location from Intent
                val extractedLocation: String? = intent?.getStringExtra("Location")

                val latLongPattern = """(-?\d+\.\d+),(-?\d+\.\d+)""".toRegex()
                val matchResult = latLongPattern.find(extractedLocation ?: "")
                val latitude = matchResult?.groups?.get(1)?.value ?: "45.5017"
                val longitude = matchResult?.groups?.get(2)?.value ?: "-73.5673"

                val location = "$latitude,$longitude"
                Log.e("ExtractedLocation", location)

                val radius = 50000 // Searching within a 5km radius, update this if you want a different value
                val keyword = s.toString()

                service.searchNearby(location, radius, keyword, "AIzaSyAUcUujvbKP4jVrmo3I00MNI8pdar4Ag0g").enqueue(object : Callback<SearchResponse> {
                    override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                        if (response.isSuccessful) {
                            Log.d("API_RESPONSE", response.toString())
                            val suggestions = response.body()?.results?.map {
                                PlaceItem(it.name, it.vicinity, it.geometry.location.lat, it.geometry.location.lng)
                            } ?: emptyList()
                            places.clear()
                            places.addAll(suggestions)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                        // Gérez l'échec ici
                    }
                })
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedPlace = adapter.getItem(position) as PlaceItem
            val intent = Intent(this@PlaceActivity, NavigationViewActivity::class.java).apply {
                putExtra("selectedAddress", selectedPlace.address)
                putExtra("latitude", selectedPlace.latitude)
                putExtra("longitude", selectedPlace.longitude)

            }
            startActivity(intent)
        }

    }

    class PlaceAdapter(context: Context, val items: MutableList<PlaceItem>) : BaseAdapter() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)

        override fun getCount(): Int = items.size

        override fun getItem(position: Int): Any = items[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: inflater.inflate(R.layout.liste_item_place, parent, false)

            val placeName = view.findViewById<TextView>(R.id.placeName)
            val placeAddress = view.findViewById<TextView>(R.id.placeAddress)

            placeName.text = items[position].name
            placeAddress.text = items[position].address

            return view
        }
    }

    data class PlaceItem(val name: String, val address: String, val latitude: Double, val longitude: Double)

}
