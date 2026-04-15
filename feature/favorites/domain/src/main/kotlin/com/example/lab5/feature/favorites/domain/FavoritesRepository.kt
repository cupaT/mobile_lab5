package com.example.lab5.feature.favorites.domain

import com.example.lab5.feature.favorites.api.FavoriteBook
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<FavoriteBook>>
    suspend fun toggleFavorite(bookId: String)
}
