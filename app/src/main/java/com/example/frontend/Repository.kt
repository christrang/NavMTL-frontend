package com.example.frontend

import android.app.Application
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class Repository(val app: Application) {
    private val queue = Volley.newRequestQueue(app)

    fun login(mail: String, mdp: String, callback: (Boolean) -> Unit) {
        val url =
            "http://10.90.68.53/auth/token"  // Remplacez par par votre addrese ip

        val json = JSONObject()
        json.put("email", mail)
        json.put("mdp", mdp)



        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            json,
            {
                // Traitement de la réponse JSON ici

                // ne stocke pas de token
                Log.d("Jeton :", it.getString("token"))
            },
            {
                // Échec de la réponse JSON, appelez le callback avec false
                Log.d("Error.Response", it.message.toString())
            }
        )


        queue.add(request)
    }
                                 /// Inscription
    fun inscription(
                                     nom: String,
                                     prenom: String, mail: String, mdp: String, number: String, callback: (Boolean) -> Unit) {
        val url =
            "http://10.90.68.53/inscription"  // Remplacez par par votre addrese ip

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
            {
                // Traitement de la réponse JSON ici

                // ne stocke pas de token
                Log.d("Jeton :", it.getString("token"))
            },
            {
                // Échec de la réponse JSON, appelez le callback avec false
                Log.d("Error.Response", it.message.toString())
            }
        )


        queue.add(request)
    }
}