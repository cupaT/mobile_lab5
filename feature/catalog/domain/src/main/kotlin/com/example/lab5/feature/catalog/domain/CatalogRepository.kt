package com.example.lab5.feature.catalog.domain

import com.example.lab5.feature.catalog.api.CatalogBook
import kotlinx.coroutines.flow.Flow

interface CatalogRepository {
    fun observeBooks(): Flow<List<CatalogBook>>
    fun observeBook(bookId: String): Flow<CatalogBook?>
    suspend fun toggleFavorite(bookId: String)
}
