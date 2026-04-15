package com.example.lab5.feature.catalog.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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

fun NavGraphBuilder.catalogGraph(
    onOpenDetails: (String) -> Unit
) {
    composable(AppDestinations.Catalog) {
        val viewModel: CatalogViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        CatalogScreen(
            state = state,
            onQueryChange = viewModel::onQueryChange,
            onOpenDetails = onOpenDetails,
            onToggleFavorite = viewModel::toggleFavorite
        )
    }
}

fun NavGraphBuilder.catalogDetailsGraph(
    onBack: () -> Unit
) {
    composable(AppDestinations.Details) { backStackEntry ->
        val bookId = backStackEntry.arguments?.getString("bookId").orEmpty()
        val viewModel: CatalogViewModel = koinViewModel()
        val state by viewModel.detailsState(bookId).collectAsStateWithLifecycle()
        CatalogDetailsScreen(
            state = state,
            onBack = onBack,
            onToggleFavorite = viewModel::toggleFavorite
        )
    }
}

@Composable
private fun CatalogScreen(
    state: CatalogState,
    onQueryChange: (String) -> Unit,
    onOpenDetails: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = AppSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Поиск по каталогу") },
            placeholder = { Text("Название, автор или жанр") }
        )

        when (val booksState = state.booksState) {
            UiState.Loading -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
            is UiState.Empty -> EmptyStateCard(booksState.title, booksState.subtitle)
            is UiState.Success -> LazyColumn(verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
                items(booksState.value, key = { it.id }) { book ->
                    CatalogBookCard(book, { onOpenDetails(book.id) }, { onToggleFavorite(book.id) })
                }
            }
        }
    }
}

@Composable
private fun CatalogBookCard(book: Book, onOpenDetails: () -> Unit, onToggleFavorite: () -> Unit) {
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
                    Icon(
                        imageVector = if (book.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Избранное"
                    )
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

@Composable
private fun CatalogDetailsScreen(
    state: CatalogDetailsState,
    onBack: () -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    when (val bookState = state.bookState) {
        UiState.Loading -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            CircularProgressIndicator()
        }
        is UiState.Empty -> EmptyStateCard(bookState.title, bookState.subtitle, modifier = Modifier.padding(AppSpacing.medium))
        is UiState.Success -> {
            val book = bookState.value
            Column(
                modifier = Modifier.padding(AppSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                    Text("Детали книги", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                    IconButton(onClick = { onToggleFavorite(book.id) }) {
                        Icon(
                            imageVector = if (book.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Избранное"
                        )
                    }
                }
                Text(book.title, style = MaterialTheme.typography.headlineMedium)
                Text("${book.author} • ${book.year}", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)) {
                    AssistChip(onClick = {}, label = { Text(book.genre) })
                    AssistChip(onClick = {}, label = { Text("Рейтинг ${book.rating}") })
                }
                Text(book.description, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
