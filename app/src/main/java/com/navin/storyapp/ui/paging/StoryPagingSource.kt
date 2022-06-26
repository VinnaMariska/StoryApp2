package com.navin.storyapp.ui.paging

import androidx.datastore.preferences.protobuf.Api
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.navin.storyapp.api.ApiService
import com.navin.storyapp.api.StoryResponse

class StoryPagingSource(private val apiService: ApiService, private val userToken: String) :
    PagingSource<Int, StoryResponse.Story>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResponse.Story> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStoryPaging("Bearer $userToken", page, params.loadSize)
            val result = responseData.listStory

            LoadResult.Page(
                data = result,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (result.isNullOrEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryResponse.Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}