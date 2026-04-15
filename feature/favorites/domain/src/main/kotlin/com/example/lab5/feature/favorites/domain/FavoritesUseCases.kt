package com.example.lab5.feature.favorites.domain

import com.example.lab5.feature.favorites.api.FavoriteBook
import kotlinx.coroutines.flow.Flow

class GetFavoritesUseCase(private val repository: FavoritesRepository) {
    operator fun invoke(): Flow<List<FavoriteBook>> = repository.observeFavorites()
}

class ToggleFavoriteUseCase(private val repository: FavoritesRepository) {
    suspend operator fun invoke(bookId: String) { repository.toggleFavorite(bookId) }
}
