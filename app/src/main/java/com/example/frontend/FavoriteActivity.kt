package com.example.frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoriteActivity : AppCompatActivity() {
    private val url = "http://navmtl-543ba0ee6069.herokuapp.com/favoris"
    private lateinit var AUTH_TOKEN: String
    private lateinit var favoriViewModel: FavoriViewModel
    private lateinit var favorisRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoris)

        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        AUTH_TOKEN = sharedPrefs.getString("token", null) ?: ""

        val backbutton = findViewById<ImageButton>(R.id.reculer)

        backbutton.setOnClickListener {
            val intent = Intent(this, NavigationViewActivity::class.java)
            startActivity(intent)
            finish()
        }

        favoriViewModel = ViewModelProvider(this).get(FavoriViewModel::class.java)

        // Set up the RecyclerView
        favorisRecyclerView = findViewById<RecyclerView>(R.id.rvFavorite)
        favorisRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with an empty array initially
        val adapter = FavorisRecyclerView(emptyArray())
        favorisRecyclerView.adapter = adapter

        // Observe the LiveData and update the RecyclerView when data changes
        favoriViewModel.listeFavori.observe(this) { favorites ->
            adapter.updateData(favorites) // Assuming your adapter has a submitList method
        }
    }
}
