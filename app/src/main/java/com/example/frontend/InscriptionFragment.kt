package com.example.frontend

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.frontend.R
import com.example.frontend.Repository

class FragmentInscription : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inscription, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = Repository(requireActivity().application)
        val button = view.findViewById<Button>(R.id.button)
        val email = view.findViewById<EditText>(R.id.inputEmail)
        val password = view.findViewById<EditText>(R.id.inputMdp)
        val nom = view.findViewById<EditText>(R.id.inputNom)
        val prenom = view.findViewById<EditText>(R.id.inputPrenom)
        val number = view.findViewById<EditText>(R.id.inputPhone)
        val button2 = view.findViewById<Button>(R.id.button2)
        val errorMessageInscription = view.findViewById<TextView>(R.id.errorMessageInscription)
        val back = view.findViewById<ImageButton>(R.id.inscriptionback)

        back.setOnClickListener {
            val intent = Intent(requireActivity(), MenuActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        button2.setOnClickListener {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        button.setOnClickListener {
            val mail = email.text.toString()
            val mdp = password.text.toString()
            val nom1 = nom.text.toString()
            val prenom1 = prenom.text.toString()
            val number1 = number.text.toString()

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
                    val intent = Intent(requireActivity(), NavigationViewActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
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
