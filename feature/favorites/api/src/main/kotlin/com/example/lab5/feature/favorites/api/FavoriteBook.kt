package com.example.lab5.feature.favorites.api

data class FavoriteBook(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val genre: String,
    val readingStatus: String,
    val isFavorite: Boolean
)
