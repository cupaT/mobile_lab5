package com.example.lab5.feature.catalog.domain

import com.example.core.books.Book

class SearchBooksUseCase {
    operator fun invoke(books: List<Book>, query: String): List<Book> {
        if (query.isBlank()) return books
        val normalizedQuery = query.trim().lowercase()
        return books.filter { book ->
            book.title.lowercase().contains(normalizedQuery) ||
                book.author.lowercase().contains(normalizedQuery) ||
                book.genre.lowercase().contains(normalizedQuery)
        }
    }
}
