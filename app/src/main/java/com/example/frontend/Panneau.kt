package com.example.frontend

data class RpaData(
    val Coordonnees: Coordonnees,
    val Description_RPA: String,
    val Resultat_Verification: String
)

data class Coordonnees(
    val Latitude: String,
    val Longitude: String
)