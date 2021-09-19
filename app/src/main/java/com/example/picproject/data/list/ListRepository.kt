package com.example.picproject.data.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.picproject.api.UnsplashApi
import com.example.picproject.ui.frgs.ListType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListRepository @Inject constructor(private val unsplashApi: UnsplashApi) {

    fun getListResults(key: String, type: ListType, sortBy: String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            ListPagingSource(unsplashApi, key, type,sortBy)
        }
    ).liveData
}