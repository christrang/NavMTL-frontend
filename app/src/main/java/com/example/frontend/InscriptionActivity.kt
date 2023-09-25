package com.example.frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class InscriptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)

        val repository = Repository(application)
        val button = findViewById<Button>(R.id.button)
        val email = findViewById<EditText>(R.id.inputEmail)
        val password = findViewById<EditText>(R.id.inputMdp)
        val nom = findViewById<EditText>(R.id.inputNom)
        val prenom = findViewById<EditText>(R.id.inputPrenom)
        val number = findViewById<EditText>(R.id.inputPhone)


        button.setOnClickListener {
            val mail = email.text.toString()
            val mdp  = password.text.toString()
            val nom1  = nom.text.toString()
            val prenom1  = prenom.text.toString()
            val number1  = number.text.toString()

            Log.e("InscriptionActivity", "Token")
            repository.inscription(nom1, prenom1,mail,mdp,number1) { response -> // ne marche pas encore
                if (response) {
                    Log.e("LoginActivity", "Token")
                    // Gérez ici la suite de l'opération après une connexion réussie
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Gérez ici les erreurs de connexion en fonction de la situation
                    Log.e("LoginActivity", mail)
                }
            }
        }
    }
}