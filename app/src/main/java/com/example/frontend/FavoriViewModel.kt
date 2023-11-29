package com.example.frontend

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriViewModel(val app: Application):AndroidViewModel(app) {
    var listeFavori = MutableLiveData<Array<Favorite>>()

    init {
        getFavorites()
    }

    private fun getFavorites() {
        viewModelScope.launch (Dispatchers.IO){
            val favorites = Repository(getApplication()).getFavoris()
            listeFavori.postValue(favorites)
        }
    }

    fun addToFavorites(address: String) {
        val currentFavorites = listeFavori.value?.toMutableList() ?: mutableListOf()

        // Add the new address to the list
        currentFavorites.add(Favorite(address))

        // Update the LiveData with the new list
        listeFavori.postValue(currentFavorites.toTypedArray())
    }
}