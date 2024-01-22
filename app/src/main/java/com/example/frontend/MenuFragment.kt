package com.example.frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class MenuFragment : Fragment() {
    private val url = "http://navmtl-543ba0ee6069.herokuapp.com/user"
    private lateinit var AUTH_TOKEN: String
    private lateinit var titlename: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        // Retrieve the token from SharedPreferences
        val sharedPrefs = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        AUTH_TOKEN = sharedPrefs.getString("token", null) ?: ""

        titlename = view.findViewById(R.id.titlename)

        val backButton = view.findViewById<ImageButton>(R.id.back)
        val profileButton = view.findViewById<Button>(R.id.profilebutton)
        val favorisButton = view.findViewById<Button>(R.id.favorisbutton)
        val connectionButton = view.findViewById<Button>(R.id.login)
        val inscriptionButton = view.findViewById<Button>(R.id.signin)
        val logoutButton = view.findViewById<Button>(R.id.logout)

        logoutButton.setOnClickListener {
            val repository = Repository(requireActivity().application)
            repository.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        backButton.setOnClickListener {
            val intent = Intent(requireContext(), NavigationViewActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        profileButton.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        favorisButton.setOnClickListener {
            val intent = Intent(requireContext(), FavoriteActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        connectionButton.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        inscriptionButton.setOnClickListener {
            val intent = Intent(requireContext(), InscriptionActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        getProfile() // Appeler la fonction pour récupérer les données

        return view
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
                        val jsonObject = JSONObject(responseData)
                        val name = jsonObject.optString("nom")

                        titlename.text = name
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Gérer les erreurs d'accès au serveur ici
            }
        }
    }
}
