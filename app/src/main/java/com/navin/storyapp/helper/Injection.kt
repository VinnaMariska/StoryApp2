package com.navin.storyapp.helper

import com.navin.storyapp.api.ApiConfig
import com.navin.storyapp.ui.paging.StoryRepository


object Injection {
    fun provideRepository(userToken: String): StoryRepository {
        val apiService = ApiConfig().getApi()
        return StoryRepository(apiService, userToken)
    }
}