package com.example.picproject.ui.vm

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.picproject.SortBy
import com.example.picproject.data.UnsplashPhoto
import com.example.picproject.data.list.ListRepository
import com.example.picproject.ui.frgs.ListType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: ListRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val filteredData: LiveData<PagingData<UnsplashPhoto>> =
        savedStateHandle.getLiveData<SortBy>("sort").switchMap {
            repository.getListResults(
                savedStateHandle.getLiveData<String>("key").value!!,
                savedStateHandle.getLiveData<ListType>("type").value!!,
                it.name
            ).cachedIn(viewModelScope)
        }


    fun setOrder(order: SortBy) {
        savedStateHandle.set("sort", order)
    }
}