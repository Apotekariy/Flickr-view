package com.example.flickrview.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FlickrResponseDto(
    val photos: PhotosDataDto,
    val stat: String
)
