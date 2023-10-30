package com.example.frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    private val url = "http://navmtl-543ba0ee6069.herokuapp.com/user"
    private lateinit var AUTH_TOKEN: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Retrieve the token from SharedPreferences
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        AUTH_TOKEN = sharedPrefs.getString("token", null) ?: ""
        val backButton = findViewById<ImageButton>(R.id.back2)
        val pagebackButton = findViewById<ImageButton>(R.id.fallback)

        backButton.setOnClickListener {
            val intent = Intent(this, NavigationViewActivity::class.java)
            startActivity(intent)
            finish()
        }

        pagebackButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
        getProfile()
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
                        val nom = jsonObject.optString("nom")
                        val prenom = jsonObject.optString("prenom")
                        val email = jsonObject.optString("email")
                        val number = jsonObject.optString("number")
                        val mdp = jsonObject.optString("mdp")

                        // Update UI elements with the fetched data
                        val lastNameTextView = findViewById<TextView>(R.id.nom)
                        val emailTextView = findViewById<TextView>(R.id.email)
                        val firstNameTextView = findViewById<TextView>(R.id.prenom)
                        val numberTextView = findViewById<TextView>(R.id.telephone)
                        val passwordTextView = findViewById<TextView>(R.id.mdp)
                        val titleNameTextView = findViewById<TextView>(R.id.name)

                        titleNameTextView.text = nom
                        lastNameTextView.text = nom
                        firstNameTextView.text = prenom
                        emailTextView.text = email
                        numberTextView.text = number
                        passwordTextView.text = mdp
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
