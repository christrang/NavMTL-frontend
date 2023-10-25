package com.example.frontend

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleMapsService {
    @GET("place/nearbysearch/json")
    fun searchNearby(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("keyword") keyword: String,
        @Query("key") apiKey: String
    ): Call<SearchResponse>
}
