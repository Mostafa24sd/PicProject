package com.example.picproject.data.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.picproject.SortBy
import com.example.picproject.api.UnsplashApi
import com.example.picproject.data.UnsplashPhoto
import retrofit2.HttpException
import java.io.IOException

class SearchPagingSource(
    private val unsplashApi: UnsplashApi,
    private val query: String,
    private val sortBy: String
) : PagingSource<Int, UnsplashPhoto>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val position = params.key ?: 1

        return try {
            val response = unsplashApi.searchPhoto(query, position, params.loadSize,sortBy.lowercase())
            val photos=response.results

            LoadResult.Page(
                data = photos,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (response.results.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashPhoto>): Int? {
        return state.anchorPosition
    }
}