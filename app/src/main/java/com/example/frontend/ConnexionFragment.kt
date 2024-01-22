package com.example.frontend

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class ConnexionFragment : Fragment() {
    private lateinit var timer: CountDownTimer
    private lateinit var repository: Repository // Declare repository as a property

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_connexion, container, false)

        repository = Repository(requireActivity().application) // Initialize the repository

        val button = view.findViewById<Button>(R.id.button)
        val email = view.findViewById<EditText>(R.id.inputEmail)
        val password = view.findViewById<EditText>(R.id.inputMdp)
        val button3 = view.findViewById<Button>(R.id.button3)

        button3.setOnClickListener {
            val intent = Intent(requireActivity(), InscriptionActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        button.setOnClickListener {
            val mail = email.text.toString()
            val mdp = password.text.toString()

            if (mail.isEmpty()) {
                email.error = "L'adresse e-mail est requise"
                return@setOnClickListener
            }

            if (mdp.isEmpty()) {
                password.error = "Le mot de passe est requis"
                return@setOnClickListener
            }

            repository.login(mail, mdp) { success ->
                if (success) {
                    Log.d("ConnexionFragment", "Connexion réussie")

                    val intent = Intent(requireActivity(), ProfileActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Log.d("ConnexionFragment", "Connexion échouée")

                    val errorMessageTextView = view.findViewById<TextView>(R.id.errorMessage)
                    errorMessageTextView.text = "La connexion a échoué. Veuillez réessayer."
                    errorMessageTextView.visibility = View.VISIBLE

                    timer = object : CountDownTimer(3000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            // Tick function if needed
                        }

                        override fun onFinish() {
                            errorMessageTextView.visibility = View.GONE
                        }
                    }
                    timer.start()
                }
            }
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }
}
