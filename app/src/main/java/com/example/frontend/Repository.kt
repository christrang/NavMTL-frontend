package com.example.frontend

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class Repository(val app: Application) {
    private val queue = Volley.newRequestQueue(app)
    private val sharedPrefs: SharedPreferences =
        app.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun login(mail: String, mdp: String, callback: (Boolean) -> Unit) {
        val url = "http://192.168.5.20/auth/token"  // Remplacez par votre adresse IP

        val json = JSONObject()
        json.put("email", mail)
        json.put("mdp", mdp)

        val request = JsonObjectRequest(
            Request.Method.POST,
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


    /// Inscription
    fun inscription(
        nom: String,
        prenom: String, mail: String, mdp: String, number: String, callback: (Boolean) -> Unit
    ) {
        val url =
            "http://192.168.5.20/inscription"  // Remplacez par par votre addrese ip

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

    fun getPanneaux(callback: PanneauxCallback) {
        val url = "https://donnees.montreal.ca/api/3/action/datastore_search?resource_id=7f1d4ae9-1a12-46d7-953e-6b9c18c78680&limit=1000000"

        // Create a Volley request queue with the provided context
        val queue = Volley.newRequestQueue(app)

        // Create a GET request
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                // Process the JSON response here
                val recordsArray = response.getJSONObject("result").getJSONArray("records")
                val panneauxList = mutableListOf<Panneau>() // Créez une liste de Panneau (vous devrez créer la classe Panneau)

                for (i in 0 until recordsArray.length()) {
                    val record = recordsArray.getJSONObject(i)
                    val descriptionRpa = record.getString("DESCRIPTION_RPA")
                    val longitude = record.getString("Longitude")
                    val latitude = record.getString("Latitude")
                    val flechePan = record.getString("FLECHE_PAN") // Récupérez la valeur de la flèche du panneau depuis le JSON

                    // Créez un objet Panneau avec les données extraites
                    val panneau = Panneau(descriptionRpa, longitude, latitude, flechePan)
                    panneauxList.add(panneau)
                }

                callback.onSuccess(panneauxList) // Passez la liste des panneaux à la callback
            },
            { error ->
                callback.onError(error.message.toString())
            }
        )

        // Add the request to the queue to execute it
        queue.add(request)
    }
}