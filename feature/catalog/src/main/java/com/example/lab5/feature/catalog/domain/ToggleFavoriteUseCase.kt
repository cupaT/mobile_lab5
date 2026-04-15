package com.example.lab5.feature.catalog.domain

import com.example.core.books.BookRepository

class ToggleFavoriteUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(bookId: String) {
        repository.toggleFavorite(bookId)
    }
}
