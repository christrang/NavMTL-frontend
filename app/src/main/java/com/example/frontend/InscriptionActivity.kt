package com.example.frontend

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
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
        val button2 = findViewById<Button>(R.id.button2)
        val errorMessageInscription = findViewById<TextView>(R.id.errorMessageInscription)
        val back = findViewById<ImageButton>(R.id.inscriptionback)

        back.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        button2.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        button.setOnClickListener {
            val mail = email.text.toString()
            val mdp  = password.text.toString()
            val nom1  = nom.text.toString()
            val prenom1  = prenom.text.toString()
            val number1  = number.text.toString()

            // Vérification si l'adresse e-mail est vide
            if (mail.isEmpty()) {
                email.error = "L'adresse e-mail est requise"
                return@setOnClickListener
            }

            // Vérification si le mot de passe est vide
            if (mdp.isEmpty()) {
                password.error = "Le mot de passe est requis"
                return@setOnClickListener
            }

            // Vérification si le nom est vide
            if (nom1.isEmpty()) {
                nom.error = "Le nom est requis"
                return@setOnClickListener
            }

            // Vérification si le prénom est vide
            if (prenom1.isEmpty()) {
                prenom.error = "Le prénom est requis"
                return@setOnClickListener
            }

            // Vérification si le numéro est vide
            if (number1.isEmpty()) {
                number.error = "Le numéro est requis"
                return@setOnClickListener
            }

            Log.e("InscriptionActivity", "Token")
            repository.inscription(nom1, prenom1, mail, mdp, number1) { response ->
                if (response) {
                    Log.e("InscriptionActivity", "Inscription réussie")
                    // Gérez ici la suite de l'opération après une inscription réussie
                    val intent = Intent(this, NavigationViewActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // L'inscription a échoué, afficher un message d'erreur
                    errorMessageInscription.text = "L'inscription a échoué. Veuillez réessayer."
                    errorMessageInscription.visibility = View.VISIBLE

                    // Créez un gestionnaire de délai pour masquer le message après 3 secondes
                    val handler = Handler()
                    handler.postDelayed({
                        errorMessageInscription.visibility = View.GONE
                    }, 3000) // 3000 millisecondes (3 secondes)
                }
            }
        }
    }
}
