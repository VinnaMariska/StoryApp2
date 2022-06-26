package com.navin.storyapp.ui.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.navin.storyapp.api.ApiConfig
import com.navin.storyapp.api.UploadResponse
import com.navin.storyapp.helper.Event
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> = _isLoading

    private val _isFailed = MutableLiveData<Event<Boolean>>()
    val isFailed: LiveData<Event<Boolean>> = _isFailed

    private val _isUploadSuccess = MutableLiveData<Event<Boolean>>()
    val isUploadSuccess: LiveData<Event<Boolean>> = _isUploadSuccess

    fun uploadStory(token: String, image: MultipartBody.Part, description: RequestBody, lat: Float?, lon: Float?) {
        _isLoading.value = Event(true)
        val service = ApiConfig().getApi().uploadStory("Bearer $token", image, description, lat, lon)
        service.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                _isLoading.value = Event(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _isUploadSuccess.value = Event(true)
                    }
                } else {
                    _isUploadSuccess.value = Event(false)
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                _isLoading.value = Event(false)
                _isFailed.value = Event(true)
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "AddStoryViewModel"
    }
}