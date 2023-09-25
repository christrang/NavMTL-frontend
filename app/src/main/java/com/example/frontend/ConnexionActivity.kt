package com.example.frontend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connexion)

        val authRepository = AuthRepository(application)
        val button = findViewById<Button>(R.id.button)
        val email = findViewById<EditText>(R.id.inputEmail)
        val password = findViewById<EditText>(R.id.inputMdp)

       button.setOnClickListener {
             val mail = email.text.toString()
             val mdp  = password.text.toString()

            authRepository.login(mail, mdp) { response -> // ne marche pas encore
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

