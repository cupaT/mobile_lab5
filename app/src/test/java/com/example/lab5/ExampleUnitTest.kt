package com.example.lab5

import com.example.core.store.InMemoryBookStore
import com.example.lab5.feature.catalog.data.CatalogRepositoryImpl
import com.example.lab5.feature.catalog.domain.GetCatalogBookDetailsUseCase
import com.example.lab5.feature.catalog.domain.GetCatalogBooksUseCase
import com.example.lab5.feature.catalog.domain.SearchCatalogBooksUseCase
import com.example.lab5.feature.catalog.domain.ToggleCatalogFavoriteUseCase
import com.example.lab5.feature.favorites.data.FavoritesRepositoryImpl
import com.example.lab5.feature.favorites.domain.GetFavoritesUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    private val bookStore = InMemoryBookStore()
    private val catalogRepository = CatalogRepositoryImpl(bookStore)
    private val favoritesRepository = FavoritesRepositoryImpl(bookStore)

    @Test
    fun getBooks_returnsSeededItems() = runTest {
        val useCase = GetCatalogBooksUseCase(catalogRepository)

        val books = useCase().first()

        assertEquals(8, books.size)
    }

    @Test
    fun getBookDetails_returnsExpectedBook() = runTest {
        val useCase = GetCatalogBookDetailsUseCase(catalogRepository)

        val book = useCase("clean-architecture").first()

        assertEquals("Clean Architecture", book?.title)
    }

    @Test
    fun toggleFavorite_updatesRepositoryState() = runTest {
        val useCase = ToggleCatalogFavoriteUseCase(catalogRepository)

        useCase("effective-kotlin")
        val updatedBook = catalogRepository.observeBook("effective-kotlin").first()

        assertTrue(updatedBook?.isFavorite == true)
    }

    @Test
    fun getFavoriteBooks_returnsOnlyFavorites() = runTest {
        val useCase = GetFavoritesUseCase(favoritesRepository)

        val favorites = useCase().first()

        assertTrue(favorites.all { it.isFavorite })
        assertFalse(favorites.isEmpty())
    }

    @Test
    fun searchBooks_filtersByTitleAuthorAndGenre() = runTest {
        val useCase = SearchCatalogBooksUseCase()
        val allBooks = catalogRepository.observeBooks().first()

        val byTitle = useCase(allBooks, "clean")
        val byAuthor = useCase(allBooks, "fowler")
        val byGenre = useCase(allBooks, "systems")

        assertEquals(1, byTitle.size)
        assertEquals("Clean Architecture", byTitle.first().title)
        assertEquals("Refactoring", byAuthor.first().title)
        assertEquals("Designing Data-Intensive Applications", byGenre.first().title)
    }
}
