package com.example.flickrview.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.flickrview.domain.model.Photo
import com.example.flickrview.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoListViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _photos = MutableStateFlow<PagingData<Photo>>(PagingData.empty())
    val photos: StateFlow<PagingData<Photo>> = _photos.asStateFlow()

    private var currentSearchJob: Job? = null

    init {
        searchPhotos("cats")
    }

    fun searchPhotos(query: String) {
        currentSearchJob?.cancel()

        currentSearchJob = viewModelScope.launch {
            _photos.value = PagingData.empty()

            photoRepository.searchPhotos(query)
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _photos.value = pagingData
                }
        }
    }
}