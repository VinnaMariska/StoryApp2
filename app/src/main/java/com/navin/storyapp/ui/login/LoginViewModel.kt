package com.navin.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navin.storyapp.api.ApiConfig
import com.navin.storyapp.api.LoginRequest
import com.navin.storyapp.api.LoginResponse
import com.navin.storyapp.helper.Event
import com.navin.storyapp.model.UserModel
import com.navin.storyapp.model.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> = _isLoading

    private val _isFailed = MutableLiveData<Event<Boolean>>()
    val isFailed: LiveData<Event<Boolean>> = _isFailed

    private val _isSuccess = MutableLiveData<Event<Boolean>>()
    val isSuccess: LiveData<Event<Boolean>> = _isSuccess

    private fun login(user: UserModel) {
        viewModelScope.launch {
            pref.login(user)
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = Event(true)
        val service = ApiConfig().getApi().loginUser(LoginRequest(email, password))
        service.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = Event(false)
                val loginResponse = response.body()
                if (response.isSuccessful) {
                    val userId = loginResponse?.loginResult?.userId
                    val name = loginResponse?.loginResult?.name
                    val token = loginResponse?.loginResult?.token

                    login(UserModel(userId!!, name!!, token!!,true))

                    _isSuccess.value = Event(true)
                } else {
                    _isSuccess.value = Event(false)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = Event(false)
                _isFailed.value = Event(true)
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}