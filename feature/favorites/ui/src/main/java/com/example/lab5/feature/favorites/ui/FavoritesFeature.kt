package com.example.lab5.feature.favorites.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.core.common.AppSpacing
import com.example.core.common.UiState
import com.example.core.ui.EmptyStateCard
import com.example.lab5.core.navigation.AppDestinations
import com.example.lab5.feature.favorites.api.FavoriteBook
import com.example.lab5.feature.favorites.domain.GetFavoritesUseCase
import com.example.lab5.feature.favorites.domain.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

data class FavoritesState(val booksState: UiState<List<FavoriteBook>> = UiState.Loading)

class FavoritesViewModel(
    getFavorites: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    val state: StateFlow<FavoritesState> = getFavorites()
        .map { books -> FavoritesState(if (books.isEmpty()) UiState.Empty("Пока нет избранного", "Добавь книги из каталога.") else UiState.Success(books)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoritesState())

    fun toggleFavorite(bookId: String) { viewModelScope.launch { toggleFavoriteUseCase(bookId) } }
}

val favoritesUiModule = module {
    factory { GetFavoritesUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
    viewModel { FavoritesViewModel(get(), get()) }
}

fun NavGraphBuilder.favoritesGraph(onOpenDetails: (String) -> Unit) {
    composable(AppDestinations.Favorites) {
        val viewModel: FavoritesViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        when (val booksState = state.booksState) {
            UiState.Loading -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { CircularProgressIndicator() }
            is UiState.Empty -> EmptyStateCard(booksState.title, booksState.subtitle, modifier = Modifier.padding(AppSpacing.medium))
            is UiState.Success -> LazyColumn(modifier = Modifier.padding(horizontal = AppSpacing.medium), verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
                items(booksState.value, key = { it.id }) { book ->
                    Card(modifier = Modifier.fillMaxWidth().clickable { onOpenDetails(book.id) }) {
                        Column(modifier = Modifier.padding(AppSpacing.medium), verticalArrangement = Arrangement.spacedBy(AppSpacing.small)) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.extraSmall)) {
                                    Text(book.title, style = MaterialTheme.typography.titleMedium)
                                    Text(book.author, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { viewModel.toggleFavorite(book.id) }) {
                                    Icon(Icons.Filled.Favorite, contentDescription = "Удалить из избранного")
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)) {
                                AssistChip(onClick = {}, label = { Text(book.genre) })
                                AssistChip(onClick = {}, label = { Text(book.readingStatus) })
                            }
                            Text(book.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
