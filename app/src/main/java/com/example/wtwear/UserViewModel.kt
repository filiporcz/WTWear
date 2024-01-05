package com.example.wtwear

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtwear.backend.data.Clothes
import com.example.wtwear.backend.data.User
import com.example.wtwear.backend.data.WSWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {
    val user: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    val weather: MutableLiveData<WSWeather> by lazy {
        MutableLiveData<WSWeather>()
    }

    val clothes: MutableLiveData<Clothes> by lazy {
        MutableLiveData<Clothes>()
    }

    fun initOrUpdate(latitude: String, longitude: String, gender: String?) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            user.postValue(User(
                latitude,
                longitude,
                gender
            ))

            withContext(Dispatchers.Main) {
                Log.d("UserViewModel.initOrUpdate() STATUS:", user.value.toString())
            }
        }
    }
}