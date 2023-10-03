package com.example.frontend

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

class MenuActivity: AppCompatActivity() {
    private val url = "http://192.168.5.20/user"
    private lateinit var AUTH_TOKEN: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Retrieve the token from SharedPreferences
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        AUTH_TOKEN = sharedPrefs.getString("token", null) ?: ""
        val backButton = findViewById<ImageButton>(R.id.back)
        val profileButton = findViewById<Button>(R.id.profilebutton)
        val favorisButton = findViewById<Button>(R.id.favorisbutton)

        //bring page back to main page
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //bring page to profile page
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        // bring page to favorites page
        favorisButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        getProfile()
    }
    private fun getProfile(){
        GlobalScope.launch(Dispatchers.IO) {
            try{
                val client = OkHttpClient()

                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $AUTH_TOKEN")
                    .build()

                val response = client.newCall(request).execute()
                val responseData = response.body?.string()

                withContext(Dispatchers.Main){
                    if (responseData != null){
                        val jsonObject = JSONObject(responseData)
                        val name = jsonObject.optString("nom")

                        val titlename = findViewById<TextView>(R.id.titlename)

                        titlename.text = name
                      }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
             }
          }
    }
}