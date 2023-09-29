package com.example.frontend

interface PanneauxCallback {
    fun onSuccess(panneaux: MutableList<Panneau>)
    fun onError(errorMessage: String)
}
