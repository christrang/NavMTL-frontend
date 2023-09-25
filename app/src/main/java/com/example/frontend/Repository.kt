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
    private val sharedPrefs: SharedPreferences = app.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

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
                                     prenom: String, mail: String, mdp: String, number: String, callback: (Boolean) -> Unit) {
        val url =
            "http://192.168.5.20/inscription"  // Remplacez par par votre addrese ip

        val json = JSONObject()
        json.put("email", mail)
        json.put("mdp", mdp)
        json.put("nom", nom)
        json.put("prenom",prenom)
        json.put("number",number)
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
            {error ->
                // Échec de la réponse JSON, appelez le callback avec false
                Log.d("Error.Response", error.message.toString())
                callback(false)
            }
        )


        queue.add(request)
    }
}