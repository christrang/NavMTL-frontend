package com.example.frontend

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(val app: Application):AndroidViewModel(app) {
    val listeHistory = MutableLiveData<Array<History>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Repository(getApplication()).getHistory(listeHistory)
        }
    }
}