package com.example.frontend

interface PanneauxCallback {
    fun onSuccess(panneaux: List<String>)
    fun onError(errorMessage: String)
}
