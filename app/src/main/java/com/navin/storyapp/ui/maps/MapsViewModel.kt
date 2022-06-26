package com.navin.storyapp.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.navin.storyapp.api.ApiConfig
import com.navin.storyapp.api.StoryResponse
import com.navin.storyapp.helper.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel : ViewModel() {
    private val _locationStory = MutableLiveData<StoryResponse>()
    val locationStory: LiveData<StoryResponse> = _locationStory

    private val _isFailed = MutableLiveData<Event<Boolean>>()
    val isFailed: LiveData<Event<Boolean>> = _isFailed

    fun getStoryList(token: String, page: Int) {
        val service = ApiConfig().getApi().getAllStoryWithLocation("Bearer $token", page, SIZE, LOCATION)
        service.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                val storyList = response.body()?.listStory
                if (!storyList.isNullOrEmpty()) {
                    _locationStory.value = response.body()
                    _isFailed.value = Event(false)
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isFailed.value = Event(true)
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "LocationMapsViewModel"
        private const val SIZE = 10
        private const val LOCATION = 1
    }
}


