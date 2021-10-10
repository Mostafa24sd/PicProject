package com.example.picproject.ui.vm

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.picproject.SortBy
import com.example.picproject.data.search.SearchRepository
import com.example.picproject.ui.frgs.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val combinedValues = MediatorLiveData<Pair<String?, SortBy?>>().apply {
        addSource(savedStateHandle.getLiveData<String>("query")) {
            value = Pair(it, savedStateHandle.getLiveData<SortBy>("sort").value)
        }
        addSource(savedStateHandle.getLiveData<SortBy>("sort")) {
            value = Pair(savedStateHandle.getLiveData<String>("query").value, it)
        }
    }

    val photos = Transformations.switchMap(combinedValues) {
        val query = it.first
        val sortBy = it.second?.name
        Log.d(TAG, "q: $query,    s:$sortBy")
        if (!query.isNullOrEmpty() && !sortBy.isNullOrEmpty()) {
            repository.getSearchResults(query, sortBy).cachedIn(viewModelScope)
        } else null
    }

    fun setQuery(query: String) {
        savedStateHandle.set("query", query)
    }

    fun setOrder(order: SortBy) {
        savedStateHandle.set("sort", order)
    }
}