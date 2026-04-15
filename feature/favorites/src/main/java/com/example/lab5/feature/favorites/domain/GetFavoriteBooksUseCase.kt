package com.example.lab5.feature.favorites.domain

import com.example.core.books.Book
import com.example.core.books.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFavoriteBooksUseCase(private val repository: BookRepository) {
    operator fun invoke(): Flow<List<Book>> = repository.observeBooks().map { books ->
        books.filter { it.isFavorite }
    }
}
