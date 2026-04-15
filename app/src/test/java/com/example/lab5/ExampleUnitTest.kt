package com.example.lab5

import com.example.lab5.feature.catalog.data.BookSeed
import com.example.lab5.feature.catalog.data.InMemoryBookRepository
import com.example.lab5.feature.catalog.domain.GetBookDetailsUseCase
import com.example.lab5.feature.catalog.domain.GetBooksUseCase
import com.example.lab5.feature.catalog.domain.SearchBooksUseCase
import com.example.lab5.feature.catalog.domain.ToggleFavoriteUseCase
import com.example.lab5.feature.favorites.domain.GetFavoriteBooksUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    private val repository = InMemoryBookRepository(BookSeed.books)

    @Test
    fun getBooks_returnsSeededItems() = runTest {
        val useCase = GetBooksUseCase(repository)

        val books = useCase().first()

        assertEquals(8, books.size)
    }

    @Test
    fun getBookDetails_returnsExpectedBook() = runTest {
        val useCase = GetBookDetailsUseCase(repository)

        val book = useCase("clean-architecture").first()

        assertEquals("Clean Architecture", book?.title)
    }

    @Test
    fun toggleFavorite_updatesRepositoryState() = runTest {
        val useCase = ToggleFavoriteUseCase(repository)

        useCase("effective-kotlin")
        val updatedBook = repository.observeBook("effective-kotlin").first()

        assertTrue(updatedBook?.isFavorite == true)
    }

    @Test
    fun getFavoriteBooks_returnsOnlyFavorites() = runTest {
        val useCase = GetFavoriteBooksUseCase(repository)

        val favorites = useCase().first()

        assertTrue(favorites.all { it.isFavorite })
        assertFalse(favorites.isEmpty())
    }

    @Test
    fun searchBooks_filtersByTitleAuthorAndGenre() {
        val useCase = SearchBooksUseCase()

        val byTitle = useCase(BookSeed.books, "clean")
        val byAuthor = useCase(BookSeed.books, "fowler")
        val byGenre = useCase(BookSeed.books, "systems")

        assertEquals(1, byTitle.size)
        assertEquals("Clean Architecture", byTitle.first().title)
        assertEquals("Refactoring", byAuthor.first().title)
        assertEquals("Designing Data-Intensive Applications", byGenre.first().title)
    }
}
