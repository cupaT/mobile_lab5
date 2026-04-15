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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.core.common.AppSpacing
import com.example.core.common.UiState
import com.example.core.ui.EmptyStateCard
import com.example.lab5.core.navigation.AppDestinations
import com.example.lab5.feature.catalog.api.CatalogBook
import com.example.lab5.feature.catalog.domain.GetCatalogBookDetailsUseCase
import com.example.lab5.feature.catalog.domain.GetCatalogBooksUseCase
import com.example.lab5.feature.catalog.domain.SearchCatalogBooksUseCase
import com.example.lab5.feature.catalog.domain.ToggleCatalogFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

data class CatalogState(val query: String = "", val booksState: UiState<List<CatalogBook>> = UiState.Loading)
data class CatalogDetailsState(val bookState: UiState<CatalogBook> = UiState.Loading)

class CatalogViewModel(
    getBooks: GetCatalogBooksUseCase,
    private val getDetails: GetCatalogBookDetailsUseCase,
    private val search: SearchCatalogBooksUseCase,
    private val toggleFavorite: ToggleCatalogFavoriteUseCase
) : ViewModel() {
    private val query = MutableStateFlow("")
    val state: StateFlow<CatalogState> = combine(getBooks(), query) { books, currentQuery ->
        val filtered = search(books, currentQuery)
        CatalogState(currentQuery, if (filtered.isEmpty()) UiState.Empty("Книги не найдены", "Попробуй изменить запрос.") else UiState.Success(filtered))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogState())

    fun details(bookId: String): StateFlow<CatalogDetailsState> = getDetails(bookId)
        .map { book -> CatalogDetailsState(if (book == null) UiState.Empty("Книга не найдена", "Похоже, запись исчезла.") else UiState.Success(book)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogDetailsState())

    fun onQueryChange(value: String) { query.update { value } }
    fun toggleFavorite(bookId: String) { viewModelScope.launch { toggleFavorite(bookId) } }
}

val catalogUiModule = module {
    factory { GetCatalogBooksUseCase(get()) }
    factory { GetCatalogBookDetailsUseCase(get()) }
    factory { SearchCatalogBooksUseCase() }
    factory { ToggleCatalogFavoriteUseCase(get()) }
    viewModel { CatalogViewModel(get(), get(), get(), get()) }
}

fun NavGraphBuilder.catalogGraph(onOpenDetails: (String) -> Unit) {
    composable(AppDestinations.Catalog) {
        val viewModel: CatalogViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        Column(modifier = Modifier.padding(horizontal = AppSpacing.medium), verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
            OutlinedTextField(value = state.query, onValueChange = viewModel::onQueryChange, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("Поиск по каталогу") })
            when (val booksState = state.booksState) {
                UiState.Loading -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { CircularProgressIndicator() }
                is UiState.Empty -> EmptyStateCard(booksState.title, booksState.subtitle)
                is UiState.Success -> LazyColumn(verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
                    items(booksState.value, key = { it.id }) { book ->
                        Card(modifier = Modifier.fillMaxWidth().clickable { onOpenDetails(book.id) }) {
                            Column(modifier = Modifier.padding(AppSpacing.medium), verticalArrangement = Arrangement.spacedBy(AppSpacing.small)) {
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppSpacing.extraSmall)) {
                                        Text(book.title, style = MaterialTheme.typography.titleMedium)
                                        Text(book.author, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(onClick = { viewModel.toggleFavorite(book.id) }) {
                                        Icon(if (book.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, contentDescription = "Избранное")
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
}

fun NavGraphBuilder.catalogDetailsGraph(onBack: () -> Unit) {
    composable(AppDestinations.Details) { entry ->
        val bookId = entry.arguments?.getString("bookId").orEmpty()
        val viewModel: CatalogViewModel = koinViewModel()
        val state by viewModel.details(bookId).collectAsStateWithLifecycle()
        when (val bookState = state.bookState) {
            UiState.Loading -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { CircularProgressIndicator() }
            is UiState.Empty -> EmptyStateCard(bookState.title, bookState.subtitle, modifier = Modifier.padding(AppSpacing.medium))
            is UiState.Success -> {
                val book = bookState.value
                Column(modifier = Modifier.padding(AppSpacing.medium), verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад") }
                        Text("Детали книги", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.toggleFavorite(book.id) }) {
                            Icon(if (book.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, contentDescription = "Избранное")
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
}
