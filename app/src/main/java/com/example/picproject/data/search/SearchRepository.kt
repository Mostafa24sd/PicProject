package com.example.picproject.data.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.picproject.api.UnsplashApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(private val unsplashApi: UnsplashApi) {

    fun getSearchResults(query: String, sortBy: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            SearchPagingSource(unsplashApi, query,sortBy)
        }
    ).liveData
}