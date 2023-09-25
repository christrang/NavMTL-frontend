package com.example.frontend

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.os.CountDownTimer

class LoginActivity : AppCompatActivity() {
    private lateinit var timer: CountDownTimer // Déclarez une variable pour le timer

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connexion)

        val repository = Repository(application)
        val button = findViewById<Button>(R.id.button)
        val email = findViewById<EditText>(R.id.inputEmail)
        val password = findViewById<EditText>(R.id.inputMdp)
        val button3 = findViewById<Button>(R.id.button3)

        button3.setOnClickListener {
            val intent = Intent(this, InscriptionActivity::class.java)
            startActivity(intent)
            finish()
        }

        button.setOnClickListener {
            val mail = email.text.toString()
            val mdp = password.text.toString()

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

            repository.login(mail, mdp) { success ->
                if (success) {
                    // La connexion a réussi
                    Log.d("LoginActivity", "Connexion réussie")

                    // Gérez ici la suite de l'opération après une connexion réussie
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // La connexion a échoué
                    Log.d("LoginActivity", "Connexion échouée")

                    // Afficher un message d'erreur
                    val errorMessageTextView = findViewById<TextView>(R.id.errorMessage)
                    errorMessageTextView.text = "La connexion a échoué. Veuillez réessayer."
                    errorMessageTextView.visibility = View.VISIBLE

                    // Lancer le timer de 5 secondes pour masquer le message d'erreur
                    timer = object : CountDownTimer(3000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            // Cette fonction est appelée à chaque seconde restante (peut être utilisée pour une mise à jour visuelle si nécessaire)
                        }

                        override fun onFinish() {
                            // Cette fonction est appelée lorsque le compteur se termine (après 5 secondes)
                            errorMessageTextView.visibility = View.GONE
                        }
                    }
                    timer.start() // Démarrez le timer
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Assurez-vous d'annuler le timer lorsque l'activité est détruite pour éviter des fuites de mémoire
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }
}

