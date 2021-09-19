package com.example.picproject.ui.vm

import androidx.lifecycle.*
import com.example.picproject.data.UnsplashPhoto
import com.example.picproject.data.saved.DetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: DetailRepository,
    private val handle: SavedStateHandle
) : ViewModel() {

    fun getPhotoByID(id: String) = repository.getPhotoByID(id)

    fun getPhotoFromDB(id: String) = repository.getPhotoFromDB(id).asLiveData()

    val status = MutableLiveData<Event<String>>()

    fun savePhoto(photo: UnsplashPhoto) {
        viewModelScope.launch {
            repository.savePhoto(photo)
            withContext(Main) {
                status.value = Event("Saved")
            }
        }
    }

    fun removePhoto(id: String) {
        viewModelScope.launch {
            repository.removePhoto(id)
            withContext(Main) {
                status.value = Event("Removed")
            }
        }
    }

}