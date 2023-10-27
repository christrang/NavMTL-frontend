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
        viewModelScope.launch (Dispatchers.IO){
            Repository(getApplication()).getFavoris(listeFavori)
        }
    }
}