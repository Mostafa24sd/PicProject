package com.example.picproject.ui.vm

import androidx.lifecycle.ViewModel
import com.example.picproject.data.topic.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {

    val topic = homeRepository.getTopics()
}