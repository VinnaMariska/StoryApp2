package com.navin.storyapp.ui.paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.navin.storyapp.api.ApiService
import com.navin.storyapp.api.StoryResponse

class StoryRepository(private val api: ApiService, private val userToken: String) {
    fun getStory(): LiveData<PagingData<StoryResponse.Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                initialLoadSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(api, userToken)
            }
        ).liveData
    }
}