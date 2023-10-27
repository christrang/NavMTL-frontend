package com.example.frontend

data class SearchResponse(
    val results: List<Place>
)

data class Place(
    val name: String,
    val vicinity: String,
    val geometry: Geometry
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)
