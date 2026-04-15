package com.example.lab5.feature.catalog.domain

import com.example.core.books.Book
import com.example.core.books.BookRepository
import kotlinx.coroutines.flow.Flow

class GetBookDetailsUseCase(private val repository: BookRepository) {
    operator fun invoke(bookId: String): Flow<Book?> = repository.observeBook(bookId)
}
