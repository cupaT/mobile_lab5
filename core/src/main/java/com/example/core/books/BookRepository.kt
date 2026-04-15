package com.example.core.books

import kotlinx.coroutines.flow.Flow

interface BookRepository {
    fun observeBooks(): Flow<List<Book>>
    fun observeBook(bookId: String): Flow<Book?>
    suspend fun toggleFavorite(bookId: String)
}
