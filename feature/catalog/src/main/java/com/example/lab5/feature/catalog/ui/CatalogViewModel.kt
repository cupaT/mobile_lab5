package com.example.lab5.feature.catalog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.books.Book
import com.example.core.common.UiState
import com.example.lab5.feature.catalog.domain.GetBookDetailsUseCase
import com.example.lab5.feature.catalog.domain.GetBooksUseCase
import com.example.lab5.feature.catalog.domain.SearchBooksUseCase
import com.example.lab5.feature.catalog.domain.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogState(
    val query: String = "",
    val booksState: UiState<List<Book>> = UiState.Loading
)

data class CatalogDetailsState(
    val bookState: UiState<Book> = UiState.Loading
)

class CatalogViewModel(
    getBooks: GetBooksUseCase,
    private val getDetails: GetBookDetailsUseCase,
    private val searchBooks: SearchBooksUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase
) : ViewModel() {
    private val query = MutableStateFlow("")

    val state: StateFlow<CatalogState> = combine(getBooks(), query) { books, currentQuery ->
        val filtered = searchBooks(books, currentQuery)
        CatalogState(
            query = currentQuery,
            booksState = if (filtered.isEmpty()) {
                UiState.Empty("Книги не найдены", "Попробуй изменить запрос или очистить поиск.")
            } else {
                UiState.Success(filtered)
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogState())

    fun detailsState(bookId: String): StateFlow<CatalogDetailsState> {
        return getDetails(bookId)
            .map { book ->
                CatalogDetailsState(
                    bookState = if (book == null) {
                        UiState.Empty("Книга не найдена", "Похоже, эта запись больше недоступна.")
                    } else {
                        UiState.Success(book)
                    }
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogDetailsState())
    }

    fun onQueryChange(value: String) {
        query.update { value }
    }

    fun toggleFavorite(bookId: String) {
        viewModelScope.launch { toggleFavorite(bookId) }
    }
}
