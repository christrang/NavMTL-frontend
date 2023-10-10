package com.example.frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class FriendActivity : AppCompatActivity() {

    private val baseUrl = "https://navmtl-543ba0ee6069.herokuapp.com/friend"
    private lateinit var AUTH_TOKEN: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)

        // Retrieve the token from SharedPreferences
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        AUTH_TOKEN = sharedPrefs.getString("token", null) ?: ""

        val backButton = findViewById<ImageButton>(R.id.back3)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val addFriendButton = findViewById<Button>(R.id.addFriendButton)
        val removeFriendButton = findViewById<Button>(R.id.removeFriendButton)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val friendListTextView = findViewById<TextView>(R.id.friendListTextView)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            searchFriends(query)
        }

        addFriendButton.setOnClickListener {
            val userId = searchEditText.text.toString().toIntOrNull()
            if (userId != null) {
                addFriend(userId)
            } else {
                // Handle invalid input, e.g., show an error message
            }
        }

        removeFriendButton.setOnClickListener {
            val friendId = searchEditText.text.toString().toIntOrNull()
            if (friendId != null) {
                removeFriend(friendId)
            } else {
                // Handle invalid input, e.g., show an error message
            }
        }


        // Load initial friend list
        loadFriendList()
    }

    private fun searchFriends(query: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("$baseUrl/users")
                    .header("Authorization", "Bearer $AUTH_TOKEN")
                    .build()

                val response = client.newCall(request).execute()
                handleSearchFriendsResponse(response, query)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun handleSearchFriendsResponse(response: Response, query: String) {
        val responseData = response.body?.string()

        withContext(Dispatchers.Main) {
            if (response.isSuccessful && responseData != null) {
                val jsonArray = JSONArray(responseData)

                // Iterate through the array to find the user with the matching userID
                var foundUser: JSONObject? = null
                for (i in 0 until jsonArray.length()) {
                    val user = jsonArray.getJSONObject(i)
                    val userId = user.optString("userID")

                    // Convert userID to an integer for comparison
                    if (userId.toIntOrNull() == query.toIntOrNull()) {
                        foundUser = user
                        break
                    }
                }

                if (foundUser != null) {
                    // User with matching userID found
                    val userId = foundUser.optInt("userID")
                    val email = foundUser.optString("email")

                    // Update the UI to display the user details
                    val searchResultTextView = findViewById<TextView>(R.id.searchResultTextView)
                    searchResultTextView.text = "User ID: $userId\nEmail: $email"
                } else {
                    // No user with the specified userID found
                    val searchResultTextView = findViewById<TextView>(R.id.searchResultTextView)
                    searchResultTextView.text = "User not found"
                }
            } else {
                // Handle the error response
                // Example: displayErrorMessage(response.message)

                // Clear the search result TextView if there's an error
                val searchResultTextView = findViewById<TextView>(R.id.searchResultTextView)
                searchResultTextView.text = response.body.toString()
            }
        }
    }


    private fun loadFriendList() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()

                val request = Request.Builder()
                    .url("$baseUrl/")
                    .header("Authorization", "Bearer $AUTH_TOKEN")
                    .build()

                val response = client.newCall(request).execute()
                handleFriendListResponse(response)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun handleFriendListResponse(response: Response) {
        val responseData = response.body?.string()

        withContext(Dispatchers.Main) {
            if (response.isSuccessful && responseData != null) {
                val friendNames = mutableListOf<String>()

                val jsonArray = JSONArray(responseData)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val friendName = jsonObject.optString("ami_nom") + " " +
                            jsonObject.optString("ami_prenom")
                    friendNames.add(friendName)
                }

                // Update friendListTextView with friend names
                val friendListTextView = findViewById<TextView>(R.id.friendListTextView)
                friendListTextView.text = friendNames.joinToString("\n")
            } else {
                // Handle the error response
                // Example: displayErrorMessage(response.message)
            }
        }
    }

    private fun addFriend(userId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("$baseUrl/demande-ami/$userId")
                    .header("Authorization", "Bearer $AUTH_TOKEN")
                    .build()

                val response = client.newCall(request).execute()
                handleAddFriendResponse(response)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun handleAddFriendResponse(response: Response) {
        // Handle the response for adding a friend
        // Example: displaySuccessMessageOrError(response)
    }

    private fun removeFriend(friendId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("$baseUrl/$friendId")
                    .header("Authorization", "Bearer $AUTH_TOKEN")
                    .delete()
                    .build()

                val response = client.newCall(request).execute()
                handleRemoveFriendResponse(response)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun handleRemoveFriendResponse(response: Response) {
        // Handle the response for removing a friend
        // Example: displaySuccessMessageOrError(response)
    }
}
