package com.example.frontend

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request as OkHttpRequest

import org.json.JSONObject
import android.os.Handler
import android.os.Looper
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import dagger.Reusable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class Repository(val app: Application) {
    private val queue = Volley.newRequestQueue(app)
    private val sharedPrefs: SharedPreferences =
        app.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private lateinit var AUTH_TOKEN: String

    fun login(mail: String, mdp: String, callback: (Boolean) -> Unit) {
        val url = "https://navmtl-543ba0ee6069.herokuapp.com/auth/token"  // Remplacez par votre adresse IP

        val json = JSONObject()
        json.put("email", mail)
        json.put("mdp", mdp)

        val request = JsonObjectRequest(-
            com.android.volley.Request.Method.POST,
            url,
            json,
            { response ->
                // Traitement de la réponse JSON ici

                // Récupérer le token depuis la réponse
                val token = response.getString("token")
                Log.d("Etoken", response.getString("token"))
                // Stocker le token dans SharedPreferences
                val editor = sharedPrefs.edit()
                editor.putString("token", token)
                editor.apply()

                // Appeler le callback avec succès (true)
                callback(true)
            },
            { error ->
                // Échec de la réponse JSON, appelez le callback avec échec (false)
                Log.d("Error.Response", error.message.toString())

                // Appeler le callback avec échec (false)
                callback(false)
            }
        )

        queue.add(request)
    }

    // Function to log out and remove the token from SharedPreferences
    fun logout() {
        val editor = sharedPrefs.edit()
        editor.remove("token")
        editor.apply()
    }

    /// Inscription
    fun inscription(
        nom: String,
        prenom: String, mail: String, mdp: String, number: String, callback: (Boolean) -> Unit
    ) {
        val url =
            "https://navmtl-543ba0ee6069.herokuapp.com/inscription"  // Remplacez par par votre addrese ip

        val json = JSONObject()
        json.put("email", mail)
        json.put("mdp", mdp)
        json.put("nom", nom)
        json.put("prenom", prenom)
        json.put("number", number)
        Log.d("Jeton :", json.toString())
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            json,
            { response ->
                // Traitement de la réponse JSON ici
                val token = response.getString("token")
                Log.d("Etoken", response.getString("token"))
                // Stocker le token dans SharedPreferences
                val editor = sharedPrefs.edit()
                editor.putString("token", token)
                editor.apply()
                // ne stocke pas de token
                Log.d("Jeton :", response.getString("token"))
                callback(true)
            },
            { error ->
                // Échec de la réponse JSON, appelez le callback avec false
                Log.d("Error.Response", error.message.toString())
                callback(false)
            }
        )


        queue.add(request)
    }

    // Obtenir les panneaux
    fun main(listePanneau: MutableLiveData<List<RpaData>>, latitude: Double, longitude: Double) {
        // Formatez l'URL avec les coordonnées de l'utilisateur
        val url = "https://navmtl-543ba0ee6069.herokuapp.com/panneau/run?lat=$latitude&long=$longitude"

        // Créez une requête GET
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.MINUTES)
            .build()

        val request = OkHttpRequest.Builder()
            .url(url)
            .get()
            .build()

        // Exécutez la requête dans un thread séparé pour ne pas bloquer le thread principal
        Thread {
            val response = client.newCall(request).execute()

            // Vérifiez si la requête a réussi
            if (response.isSuccessful) {
                val responseBody = response.body?.string()

                // Utilisez Gson pour désérialiser la réponse JSON en une liste de RpaData
                val gson = Gson()
                val listType = object : TypeToken<List<RpaData>>() {}.type
                val rpaDataList = gson.fromJson<List<RpaData>>(responseBody, listType)

                // Obtenez un Handler pour le thread principal (UI thread)
                val mainHandler = Handler(Looper.getMainLooper())

                // Mettez à jour la valeur de listePanneau avec les données obtenues
                mainHandler.post {
                    listePanneau.postValue(rpaDataList)
                }
            } else {
                println("La requête a échoué avec le code d'état : ${response.code}")
            }
        }.start()
    }


    fun getHistory(listeHistory: MutableLiveData<Array<History>>) {
        val url = "https://navmtl-543ba0ee6069.herokuapp.com/history"

        val queue = Volley.newRequestQueue(app)
        val request = object : StringRequest(
            Method.GET,
            url,
            { response ->
                val gson = Gson()
                val history = gson.fromJson(response, Array<History>::class.java)
                listeHistory.postValue(history)
            },
            { error ->
                Log.d("GetHistory", "Erreur lors de la requête Volley : ${error.message}")
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                // Add Bearer token to the Authorization header
                headers["Authorization"] = "Bearer $AUTH_TOKEN"
                return headers
            }
        }

        queue.add(request)
    }


    fun getFavoris(listeFavoris: MutableLiveData<Array<Favorite>>) {
        val url = "https://navmtl-543ba0ee6069.herokuapp.com/favori"

        val queue = Volley.newRequestQueue(app)
        val request = object : StringRequest(
            Request.Method.GET,
            url,
            { response ->
                val gson = Gson()
                val favori = gson.fromJson(response, Array<Favorite>::class.java)
                listeFavoris.postValue(favori)
            },
            { error ->
                Log.d("GetFavoris", "Erreur lors de la requête Volley : ${error.message}")
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                // Add Bearer token to the Authorization header
                headers["Authorization"] = "Bearer $AUTH_TOKEN"
                return headers
            }
        }

        queue.add(request)
    }

}