package com.example.picproject.ui.vm

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.picproject.SortBy
import com.example.picproject.data.search.SearchRepository
import com.example.picproject.ui.DEFAULT_SEARCH_QUERY
import com.example.picproject.ui.frgs.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository,
    private val handle: SavedStateHandle
) : ViewModel() {

    private val currentQuery = handle.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)
    private val currentSort = handle.getLiveData(CURRENT_SORT, DEFAULT_SORT)


    private val combinedValues = MediatorLiveData<Pair<String?, String?>>().apply {
        addSource(currentQuery) {
            value = Pair(it, currentSort.value)
        }
        addSource(currentSort) {
            value = Pair(currentQuery.value, it)
        }
    }

    val photos = Transformations.switchMap(combinedValues) {
        val query = it.first
        val sortBy = it.second
        if (!query.isNullOrEmpty() && !sortBy.isNullOrEmpty()) {
            Log.d(TAG, "f: ${it.first} , s: ${it.second}")
            repository.getSearchResults(query, sortBy).cachedIn(viewModelScope)
        } else null
    }


    fun searchPhotos(query: String, sortBy: SortBy) {
        currentQuery.value = query
        currentSort.value = sortBy.name
    }

    companion object {
        private const val CURRENT_QUERY = "current_query"
        private const val CURRENT_SORT = "current_sort"
        private val DEFAULT_QUERY = DEFAULT_SEARCH_QUERY
        private val DEFAULT_SORT = SortBy.LATEST.name
    }
}