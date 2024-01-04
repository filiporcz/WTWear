package com.example.wtwear

import androidx.lifecycle.ViewModel
import com.example.wtwear.backend.data.User

class UserViewModel : ViewModel() {
    private lateinit var USER: User

    fun initOrUpdate(latitude: Double, longitude: Double, gender: String) {
        USER = User(
            latitude.toString(),
            longitude.toString(),
            gender
        )
    }

    fun getData(): User {
        return USER
    }
}