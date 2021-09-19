package com.example.picproject.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.picproject.data.UnsplashPhoto
import com.example.picproject.data.saved.SavedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: SavedRepository,
    private val handle: SavedStateHandle
) : ViewModel() {

    val photos: LiveData<List<UnsplashPhoto>> = repository.photos.asLiveData()


}