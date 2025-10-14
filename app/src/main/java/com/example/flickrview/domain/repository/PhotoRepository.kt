package com.example.flickrview.domain.repository

import androidx.paging.PagingData
import com.example.flickrview.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun searchPhotos(query: String): Flow<PagingData<Photo>>
}