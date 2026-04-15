package com.example.core.store

data class BookRecord(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val genre: String,
    val year: Int,
    val readingStatus: String,
    val rating: Double,
    val isFavorite: Boolean
)
