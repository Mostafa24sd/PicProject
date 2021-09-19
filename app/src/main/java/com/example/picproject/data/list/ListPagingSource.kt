package com.example.picproject.data.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.picproject.api.UnsplashApi
import com.example.picproject.data.UnsplashPhoto
import com.example.picproject.ui.frgs.ListType
import retrofit2.HttpException
import java.io.IOException

class ListPagingSource(
    private val unsplashApi: UnsplashApi,
    private val key: String,
    private val type: ListType,
    private val sortBy: String,
) : PagingSource<Int, UnsplashPhoto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val position = params.key ?: 1

        return try {
            val response = when (type) {
                ListType.TOPIC -> unsplashApi.topicsPhotos(
                    page = position,
                    perPage = params.loadSize,
                    id = key,
                    sortBy = sortBy.lowercase()
                )
                ListType.USER -> unsplashApi.userPhotos(
                    page = position,
                    perPage = params.loadSize,
                    username = key,
                    sortBy = sortBy.lowercase()
                )
            }

            LoadResult.Page(
                data = response,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (response.isEmpty()) null else position + 1
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