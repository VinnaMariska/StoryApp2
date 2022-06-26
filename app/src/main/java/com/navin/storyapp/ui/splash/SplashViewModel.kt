package com.navin.storyapp.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.navin.storyapp.model.UserModel
import com.navin.storyapp.model.UserPreference


class SplashViewModel(private val pref: UserPreference) : ViewModel() {
    fun getUserToken(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

}