package com.example.casaan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _mutableNotification = MutableLiveData<String>()

    val mutableNotification: LiveData<String> get() = _mutableNotification

    fun setNotificationMessage(message: String) {
        _mutableNotification.value = message
    }
}