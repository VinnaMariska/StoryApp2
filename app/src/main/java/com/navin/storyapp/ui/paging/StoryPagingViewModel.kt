package com.navin.storyapp.ui.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.navin.storyapp.api.StoryResponse
import com.navin.storyapp.helper.Injection

class StoryPagingViewModel(storyRepository: StoryRepository) : ViewModel() {

    val story: LiveData<PagingData<StoryResponse.Story>> =
        storyRepository.getStory().cachedIn(viewModelScope)

}

class ViewModelFactoryStoryPaging(private val userToken: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryPagingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryPagingViewModel(Injection.provideRepository(userToken)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}