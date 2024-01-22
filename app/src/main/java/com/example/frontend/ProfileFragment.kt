package com.example.frontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONObject

class ProfileFragment : Fragment() {
    // ...

    private lateinit var AUTH_TOKEN: String
    private val url = "http://navmtl-543ba0ee6069.herokuapp.com/user"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Récupérez le token depuis les arguments
        arguments?.let {
            AUTH_TOKEN = it.getString("token") ?: ""
        }

        fetchProfileData()
    }

    private fun fetchProfileData() {
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
                        val lastNameTextView = view?.findViewById<TextView>(R.id.nom)
                        val emailTextView = view?.findViewById<TextView>(R.id.email)
                        val firstNameTextView = view?.findViewById<TextView>(R.id.prenom)
                        val numberTextView = view?.findViewById<TextView>(R.id.telephone)
                        val passwordTextView = view?.findViewById<TextView>(R.id.mdp)
                        val titleNameTextView = view?.findViewById<TextView>(R.id.name)

                        titleNameTextView?.text = nom
                        lastNameTextView?.text = nom
                        firstNameTextView?.text = prenom
                        emailTextView?.text = email
                        numberTextView?.text = number
                        passwordTextView?.text = mdp
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(token: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString("token", token)
                }
            }
    }
}