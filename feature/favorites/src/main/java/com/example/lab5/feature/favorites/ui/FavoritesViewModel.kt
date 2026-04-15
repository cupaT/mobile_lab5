package com.example.lab5.feature.favorites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.books.Book
import com.example.core.common.UiState
import com.example.lab5.feature.favorites.domain.GetFavoriteBooksUseCase
import com.example.lab5.feature.favorites.domain.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FavoritesState(
    val booksState: UiState<List<Book>> = UiState.Loading
)

class FavoritesViewModel(
    getFavorites: GetFavoriteBooksUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    val state: StateFlow<FavoritesState> = getFavorites()
        .map { books ->
            FavoritesState(
                booksState = if (books.isEmpty()) {
                    UiState.Empty("Пока нет избранного", "Добавь книги в избранное из каталога, чтобы они появились здесь.")
                } else {
                    UiState.Success(books)
                }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritesState())

    fun toggleFavorite(bookId: String) {
        viewModelScope.launch { toggleFavoriteUseCase(bookId) }
    }
}
