package com.example.lab5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.lab5.data.InMemoryBookRepository
import com.example.lab5.domain.usecase.GetBookDetailsUseCase
import com.example.lab5.domain.usecase.GetBooksUseCase
import com.example.lab5.domain.usecase.GetFavoriteBooksUseCase
import com.example.lab5.domain.usecase.SearchBooksUseCase
import com.example.lab5.domain.usecase.ToggleFavoriteUseCase
import com.example.lab5.ui.books.BooksViewModel

object AppContainer {
    private val repository = InMemoryBookRepository()

    private val getBooksUseCase = GetBooksUseCase(repository)
    private val getBookDetailsUseCase = GetBookDetailsUseCase(repository)
    private val getFavoriteBooksUseCase = GetFavoriteBooksUseCase(repository)
    private val toggleFavoriteUseCase = ToggleFavoriteUseCase(repository)
    private val searchBooksUseCase = SearchBooksUseCase()

    val booksViewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return BooksViewModel(
                getBooks = getBooksUseCase,
                getBookDetails = getBookDetailsUseCase,
                getFavorites = getFavoriteBooksUseCase,
                toggleFavorite = toggleFavoriteUseCase,
                searchBooks = searchBooksUseCase
            ) as T
        }
    }
}
