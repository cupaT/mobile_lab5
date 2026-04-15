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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.core.books.Book
import com.example.core.common.AppSpacing
import com.example.core.common.UiState
import com.example.core.ui.EmptyStateCard
import com.example.lab5.core.navigation.AppDestinations
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

fun NavGraphBuilder.favoritesGraph(
    onOpenDetails: (String) -> Unit
) {
    composable(AppDestinations.Favorites) {
        val viewModel: FavoritesViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        FavoritesScreen(
            state = state,
            onOpenDetails = onOpenDetails,
            onToggleFavorite = viewModel::toggleFavorite
        )
    }
}

@Composable
private fun FavoritesScreen(
    state: FavoritesState,
    onOpenDetails: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    when (val booksState = state.booksState) {
        UiState.Loading -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            CircularProgressIndicator()
        }
        is UiState.Empty -> EmptyStateCard(booksState.title, booksState.subtitle, modifier = Modifier.padding(AppSpacing.medium))
        is UiState.Success -> LazyColumn(
            modifier = Modifier.padding(horizontal = AppSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
        ) {
            items(booksState.value, key = { it.id }) { book ->
                FavoriteBookCard(book, { onOpenDetails(book.id) }, { onToggleFavorite(book.id) })
            }
        }
    }
}

@Composable
private fun FavoriteBookCard(book: Book, onOpenDetails: () -> Unit, onToggleFavorite: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenDetails)
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.extraSmall)) {
                    Text(book.title, style = MaterialTheme.typography.titleMedium)
                    Text(book.author, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onToggleFavorite) {
                    Icon(Icons.Filled.Favorite, contentDescription = "Удалить из избранного")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)) {
                AssistChip(onClick = onOpenDetails, label = { Text(book.genre) })
                AssistChip(onClick = onOpenDetails, label = { Text(book.readingStatus.name.lowercase(Locale.getDefault())) })
            }
            Text(book.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
