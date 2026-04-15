package com.example.lab5.feature.catalog.domain

import com.example.lab5.feature.catalog.api.CatalogBook
import kotlinx.coroutines.flow.Flow

class GetCatalogBooksUseCase(private val repository: CatalogRepository) {
    operator fun invoke(): Flow<List<CatalogBook>> = repository.observeBooks()
}

class GetCatalogBookDetailsUseCase(private val repository: CatalogRepository) {
    operator fun invoke(bookId: String): Flow<CatalogBook?> = repository.observeBook(bookId)
}

class SearchCatalogBooksUseCase {
    operator fun invoke(books: List<CatalogBook>, query: String): List<CatalogBook> {
        if (query.isBlank()) return books
        val normalized = query.trim().lowercase()
        return books.filter {
            it.title.lowercase().contains(normalized) ||
                it.author.lowercase().contains(normalized) ||
                it.genre.lowercase().contains(normalized)
        }
    }
}

class ToggleCatalogFavoriteUseCase(private val repository: CatalogRepository) {
    suspend operator fun invoke(bookId: String) {
        repository.toggleFavorite(bookId)
    }
}
