package com.example.frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.navigation.dropin.NavigationView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
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

        backButton.setOnClickListener {
            val intent = Intent(this, NavigationViewActivity::class.java)
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
        getFriendRequests()
    }

    private fun searchFriends(query: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://navmtl-543ba0ee6069.herokuapp.com/users")
                    .header("Authorization", "Bearer $AUTH_TOKEN")
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseData = response.body?.string()

                    if (responseData != null) {
                        try {
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

                            withContext(Dispatchers.Main) {
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
                            }
                        } catch (e: JSONException) {
                            // Handle the case where the response is not a valid JSON array
                            e.printStackTrace()
                        }
                    } else {
                        // Handle the case where responseData is null
                    }
                } else {
                    // Handle the case where the response is not successful
                    // For example, you can show an error message.
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }



    private fun loadFriendList() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()

                val request = Request.Builder()
                    .url("$baseUrl")
                    .header("Authorization", "Bearer $AUTH_TOKEN")
                    .build()

                val response = client.newCall(request).execute()
                Log.d("d", request.toString())
                Log.d("d", response.toString())

                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    if (responseData != null) {
                        // Parse the response data into a list of Friend objects
                        val friendList = parseFriendList(responseData)
                        handleFriendListResponse(friendList)
                        Log.d("d", friendList.toString())
                    }
                } else {
                    // Handle the case where the response is not successful
                    // You can show an error message or handle it accordingly.
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // Function to parse the response data and return a list of Friend objects
    private fun parseFriendList(responseData: String): List<Friend> {
        val friendList = mutableListOf<Friend>()

        val jsonArray = JSONArray(responseData)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val friend = Friend(
                jsonObject.optString("ami_nom"),
                jsonObject.optString("ami_prenom")
            )
            friendList.add(friend)
        }

        return friendList
    }


    private suspend fun handleFriendListResponse(friendList: List<Friend>) {
        withContext(Dispatchers.Main) {
            // Get a reference to your RecyclerView
            val friendRecyclerView = findViewById<RecyclerView>(R.id.friendListRecyclerView)

            // Create an instance of your custom RecyclerView adapter
            val friendAdapter = FriendAdapter(friendList)  // Assuming you have a custom adapter

            // Set the adapter for the RecyclerView
            friendRecyclerView.adapter = friendAdapter

            // Optionally, you can set a layout manager for the RecyclerView, e.g., LinearLayoutManager
            //friendRecyclerView.layoutManager = LinearLayoutManager(this@YourActivity)

            // Optionally, if you have a TextView for a message when the friend list is empty
            val friendListTextView = findViewById<TextView>(R.id.friendListTextView)

            // Check if the friend list is empty
            if (friendList.isEmpty()) {
                friendListTextView.text = "No friends found."
                friendListTextView.visibility = View.VISIBLE
            } else {
                friendListTextView.visibility = View.GONE
            }
        }
    }

    private fun getFriendRequests() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("$baseUrl/demandes-en-attente")
                    .header("Authorization", "Bearer $AUTH_TOKEN")
                    .build()

                val response = client.newCall(request).execute()
                Log.d("q",response.toString())
                handleFriendRequestsResponse(response)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun handleFriendRequestsResponse(response: Response) {
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                Log.d("q", responseData.toString())
                if (responseData != null) {
                    try {
                        val friendRequests = parseFriendRequests(responseData)
                        Log.d("q", friendRequests.toString())
                        val friendRequestsRecyclerView = findViewById<RecyclerView>(R.id.pendingRequestsRecyclerView)

                        friendRequestsRecyclerView.layoutManager = LinearLayoutManager(this@FriendActivity)

                        val adapter = FriendRequestAdapter(friendRequests)
                        adapter.onAcceptButtonClickListener = object : FriendRequestAdapter.OnAcceptButtonClickListener {
                            override fun onAcceptButtonClick(friendRequest: FriendRequest) {
                                acceptFriendRequest(friendRequest.demandeID)
                            }
                        }

                        adapter.onDeclineButtonClickListener = object : FriendRequestAdapter.OnDeclineButtonClickListener {
                            override fun onDeclineButtonClick(friendRequest: FriendRequest) {
                                declineFriendRequest(friendRequest.demandeID)
                            }
                        }

                        friendRequestsRecyclerView.adapter = adapter

                        if (friendRequests.isEmpty()) {
                            // Display a message or handle as needed
                        }

                    } catch (e: JSONException) {
                        // Handle JSON parsing error
                        e.printStackTrace()
                    }
                } else {
                    // Handle the case where responseData is null
                }
            } else {
                // Handle the case where the response is not successful
                // For example, show an error message to the user.
            }
        }
    }

    private fun acceptFriendRequest(demandeID: Int) {
        Log.d("qqqqq", demandeID.toString())
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("$baseUrl/accepter-demande-ami/$demandeID")
                    .header("Authorization", "Bearer $AUTH_TOKEN")
                    .post(RequestBody.create(null, ByteArray(0))) // You can adjust the body based on the API requirements
                    .build()

                val response = client.newCall(request).execute()
                handleAcceptFriendResponse(response)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun declineFriendRequest(demandeID: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("$baseUrl/decliner-demande-ami/$demandeID")
                    .header("Authorization", "Bearer $AUTH_TOKEN")
                    .post(RequestBody.create(null, ByteArray(0))) // You can adjust the body based on the API requirements
                    .build()

                val response = client.newCall(request).execute()
                handleDeclineFriendResponse(response)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun handleAcceptFriendResponse(response: Response) {
        // Handle the response for accepting a friend request
        // Example: displaySuccessMessageOrError(response)
    }

    private fun handleDeclineFriendResponse(response: Response) {
        // Handle the response for declining a friend request
        // Example: displaySuccessMessageOrError(response)
    }
    private fun parseFriendRequests(responseData: String): List<FriendRequest> {
        val friendRequests = mutableListOf<FriendRequest>()
        val jsonArray = JSONArray(responseData)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            // Parse the friend request data and create a FriendRequest object
            val nom = jsonObject.getString("nom")
            val prenom = jsonObject.getString("prenom")
            val userID = jsonObject.getInt("userID")
            val expediteurID = jsonObject.getInt("expediteurID")
            val demandeID = jsonObject.getInt("demandeID")
            val destinataireID = jsonObject.getInt("destinataireID")

            // Create a FriendRequest object and add it to the list
            val friendRequest = FriendRequest(nom, prenom, userID, expediteurID, demandeID, destinataireID)
            friendRequests.add(friendRequest)
        }

        return friendRequests
    }


    private fun getExpediteurID(): Int? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("$baseUrl/user")
            .header("Authorization", "Bearer $AUTH_TOKEN")
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseData = response.body?.string()
            if (responseData != null) {
                try {
                    val jsonObject = JSONObject(responseData)
                    return jsonObject.optInt("userID")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }
    private fun addFriend(userId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val expediteurID = getExpediteurID()

                // Create a JSON object with the necessary data for the POST request
                val postData = JSONObject()
                postData.put("expediteurID", expediteurID)
                postData.put("destinataireID", userId)

                val requestBody = postData.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url("$baseUrl/demande-ami/$userId")
                    .header("Authorization", "Bearer $AUTH_TOKEN")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        if (responseData != null) {
                            try {
                                val jsonObject = JSONObject(responseData)
                                val message = jsonObject.optString("message")

                                // Handle the success message, e.g., display it to the user

                            } catch (e: JSONException) {
                                // Handle the case where the response is not a valid JSON object
                                e.printStackTrace()
                            }
                        } else {
                            // Handle the case where responseData is null
                        }
                    } else {
                        // Handle the case where the response is not successful
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
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
