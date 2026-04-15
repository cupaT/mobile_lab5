package com.example.core.store

import kotlinx.coroutines.flow.Flow

interface BookStore {
    fun observeBooks(): Flow<List<BookRecord>>
    fun observeBook(bookId: String): Flow<BookRecord?>
    suspend fun toggleFavorite(bookId: String)
}
