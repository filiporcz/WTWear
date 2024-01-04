package com.example.wtwear

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtwear.backend.data.Clothes
import com.example.wtwear.backend.data.User
import com.example.wtwear.backend.data.WSWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {
    //val user: LiveData<User> = MutableLiveData()
    val user: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    val weather: MutableLiveData<WSWeather> by lazy {
        MutableLiveData<WSWeather>()
    }

    val clothes: MutableLiveData<Clothes> by lazy {
        MutableLiveData<Clothes>()
    }

    fun initOrUpdate(latitude: Double, longitude: Double, gender: String) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            //user as MutableLiveData
            user.postValue(User(
                latitude.toString(),
                longitude.toString(),
                gender
            ))

            //weather.postValue(user.value!!.weatherInfo())
            //clothes.postValue(user.value!!.clothes())

            withContext(Dispatchers.Main) {
                Log.d("UserViewModel.initOrUpdate() STATUS:", user.value.toString())
                //Log.d("UserViewModel.initOrUpdate() STATUS 2:", weather.value.toString())
                //Log.d("UserViewModel.initOrUpdate() STATUS 3:", clothes.value.toString())
            }
        }
    }

    //fun fetchWeather() {
    //    viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
    //        weather.postValue(user.value!!.weatherInfo())

    //        withContext(Dispatchers.Main) {
    //            //Log.d("UserViewModel.initOrUpdate() STATUS:", user.value.toString())
    //            Log.d("UserViewModel.initOrUpdate() STATUS 2:", weather.value.toString())
    //            //Log.d("UserViewModel.initOrUpdate() STATUS 3:", clothes.value.toString())
    //        }
    //    }
    //}

    //fun fetchClothes() {
    //    viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
    //        clothes.postValue(user.)
    //    }
    //}
}