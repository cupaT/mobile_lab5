package com.example.lab5.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.UiState
import com.example.lab5.analytics.AnalyticsService
import com.example.lab5.crashreporting.CrashReporter
import com.example.lab5.domain.model.Book
import com.example.lab5.domain.usecase.GetBookDetailsUseCase
import com.example.lab5.domain.usecase.GetBooksUseCase
import com.example.lab5.domain.usecase.GetFavoriteBooksUseCase
import com.example.lab5.domain.usecase.SearchBooksUseCase
import com.example.lab5.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BooksViewModel(
    private val getBooks: GetBooksUseCase,
    private val getBookDetails: GetBookDetailsUseCase,
    private val getFavorites: GetFavoriteBooksUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val searchBooks: SearchBooksUseCase,
    private val analytics: AnalyticsService,
    private val crashReporter: CrashReporter
) : ViewModel() {
    private val query = MutableStateFlow("")

    val catalogState: StateFlow<BookListState> = combine(
        getBooks().catch { error ->
            crashReporter.recordNonFatal("Failed to observe catalog books", error)
            emit(emptyList())
        },
        query
    ) { books, currentQuery ->
        val filteredBooks = searchBooks(books, currentQuery)
        BookListState(
            query = currentQuery,
            booksState = toListState(
                items = filteredBooks,
                emptyTitle = "Книги не найдены",
                emptySubtitle = "Попробуй изменить запрос или очистить поиск."
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BookListState()
    )

    val favoritesState: StateFlow<BookListState> = combine(
        getFavorites().catch { error ->
            crashReporter.recordNonFatal("Failed to observe favorite books", error)
            emit(emptyList())
        },
        query
    ) { books, currentQuery ->
        val filteredBooks = searchBooks(books, currentQuery)
        BookListState(
            query = currentQuery,
            booksState = toListState(
                items = filteredBooks,
                emptyTitle = "Пока нет избранного",
                emptySubtitle = "Добавь книги в избранное из каталога, чтобы они появились здесь."
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BookListState()
    )

    fun detailsState(bookId: String): StateFlow<BookDetailsState> {
        crashReporter.setContext("book_details_id", bookId)
        return getBookDetails(bookId)
            .catch { error ->
                crashReporter.setContext("book_id", bookId)
                crashReporter.recordNonFatal("Failed to observe book details", error)
                emit(null)
            }
            .map { book ->
                BookDetailsState(
                    bookState = if (book == null) {
                        UiState.Empty(
                            title = "Книга не найдена",
                            subtitle = "Похоже, этой книги уже нет в локальном каталоге."
                        )
                    } else {
                        UiState.Success(book)
                    }
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = BookDetailsState()
            )
    }

    fun onQueryChange(value: String) {
        query.update { value }
        analytics.trackEvent("books_search_changed", mapOf("query" to value))
    }

    fun toggleFavorite(bookId: String) {
        viewModelScope.launch {
            runCatching {
                crashReporter.setContext("book_id", bookId)
                toggleFavorite.invoke(bookId)
            }.onSuccess {
                analytics.trackEvent("book_favorite_toggled", mapOf("book_id" to bookId))
            }.onFailure { error ->
                crashReporter.recordNonFatal("Failed to toggle favorite book", error)
            }
        }
    }

    fun trackScreenViewed(screenName: String) {
        crashReporter.setContext("screen_name", screenName)
        analytics.trackEvent("screen_viewed", mapOf("screen_name" to screenName))
    }

    private fun toListState(
        items: List<Book>,
        emptyTitle: String,
        emptySubtitle: String
    ): UiState<List<Book>> {
        return if (items.isEmpty()) {
            UiState.Empty(
                title = emptyTitle,
                subtitle = emptySubtitle
            )
        } else {
            UiState.Success(items)
        }
    }
}
