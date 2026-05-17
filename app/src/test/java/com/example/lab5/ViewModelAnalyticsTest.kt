package com.example.lab5

import android.app.Activity
import com.example.core.common.UiState
import com.example.lab5.analytics.FakeAnalyticsService
import com.example.lab5.auth.AuthProvider
import com.example.lab5.auth.AuthResult
import com.example.lab5.auth.AuthUser
import com.example.lab5.auth.FakeAuthService
import com.example.lab5.crashreporting.CrashReporter
import com.example.lab5.crashreporting.FakeCrashReporter
import com.example.lab5.crashreporting.ManualCrashTrigger
import com.example.lab5.data.BookSeed
import com.example.lab5.data.InMemoryBookRepository
import com.example.lab5.domain.model.Book
import com.example.lab5.domain.repository.BookRepository
import com.example.lab5.domain.usecase.GetBookDetailsUseCase
import com.example.lab5.domain.usecase.GetBooksUseCase
import com.example.lab5.domain.usecase.GetFavoriteBooksUseCase
import com.example.lab5.domain.usecase.SearchBooksUseCase
import com.example.lab5.domain.usecase.ToggleFavoriteUseCase
import com.example.lab5.ui.auth.LoginViewModel
import com.example.lab5.ui.books.BooksViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelAnalyticsTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun booksViewModel_tracksScreenViewed() {
        val analytics = FakeAnalyticsService()
        val crashReporter = FakeCrashReporter()
        val viewModel = booksViewModel(analytics, crashReporter)

        viewModel.trackScreenViewed("catalog")

        assertEquals("screen_viewed", analytics.events.single().name)
        assertEquals("catalog", analytics.events.single().params["screen_name"])
        assertEquals("catalog", crashReporter.context["screen_name"])
    }

    @Test
    fun booksViewModel_tracksFavoriteToggle() = runTest {
        val analytics = FakeAnalyticsService()
        val viewModel = booksViewModel(analytics, FakeCrashReporter())

        viewModel.toggleFavorite("effective-kotlin")
        advanceUntilIdle()

        val event = analytics.events.single { it.name == "book_favorite_toggled" }
        assertEquals("effective-kotlin", event.params["book_id"])
    }

    @Test
    fun booksViewModel_reportsFavoriteToggleErrorWithoutTrackingSuccess() = runTest {
        val analytics = FakeAnalyticsService()
        val crashReporter = FakeCrashReporter()
        val viewModel = booksViewModel(analytics, crashReporter)

        viewModel.toggleFavorite("missing-book")
        advanceUntilIdle()

        assertTrue(analytics.events.none { it.name == "book_favorite_toggled" })
        assertEquals("missing-book", crashReporter.context["book_id"])
        assertEquals("Failed to toggle favorite book", crashReporter.nonFatals.single().message)
    }

    @Test
    fun booksViewModel_reportsCatalogFlowErrorAndShowsEmptyState() = runTest {
        val analytics = FakeAnalyticsService()
        val crashReporter = FakeCrashReporter()
        val viewModel = booksViewModel(analytics, crashReporter, FailingBookRepository())

        val state = viewModel.catalogState.drop(1).first()

        assertTrue(state.booksState is UiState.Empty)
        assertEquals("Failed to observe catalog books", crashReporter.nonFatals.single().message)
    }

    @Test
    fun manualCrashTrigger_logsContextBeforeCrash() {
        val crashReporter = FakeCrashReporter()
        val trigger = ManualCrashTrigger(crashReporter)

        val error = runCatching {
            trigger.trigger(
                mapOf(
                    "screen_name" to "about",
                    "user_email" to "tester@example.com"
                )
            )
        }.exceptionOrNull()

        assertEquals("Manual crash from lab8 control task", error?.message)
        assertEquals("Manual crash button clicked", crashReporter.logs.single())
        assertEquals("about", crashReporter.context["screen_name"])
        assertEquals("tester@example.com", crashReporter.recordedUserId)
    }

    @Test
    fun loginViewModel_successTracksProviderAndAuthenticates() = runTest {
        val analytics = FakeAnalyticsService()
        val user = AuthUser("token", "Test User", AuthProvider.YANDEX)
        val viewModel = LoginViewModel(
            authService = FakeAuthService(AuthResult.Success(user)),
            analytics = analytics
        )

        viewModel.login(object : Activity() {}, AuthProvider.YANDEX)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isAuthenticated)
        val event = analytics.events.single { it.name == "user_logged_in" }
        assertEquals("yandex", event.params["provider"])
    }

    @Test
    fun loginViewModel_errorDoesNotAuthenticateOrTrackLogin() = runTest {
        val analytics = FakeAnalyticsService()
        val viewModel = LoginViewModel(
            authService = FakeAuthService(AuthResult.Error("failed")),
            analytics = analytics
        )

        viewModel.login(object : Activity() {}, AuthProvider.VK)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isAuthenticated)
        assertTrue(analytics.events.none { it.name == "user_logged_in" })
        assertEquals("failed", viewModel.state.value.message)
    }

    private fun booksViewModel(
        analytics: FakeAnalyticsService,
        crashReporter: CrashReporter,
        repository: BookRepository = InMemoryBookRepository(BookSeed.books)
    ): BooksViewModel {
        return BooksViewModel(
            getBooks = GetBooksUseCase(repository),
            getBookDetails = GetBookDetailsUseCase(repository),
            getFavorites = GetFavoriteBooksUseCase(repository),
            toggleFavorite = ToggleFavoriteUseCase(repository),
            searchBooks = SearchBooksUseCase(),
            analytics = analytics,
            crashReporter = crashReporter
        )
    }

    private class FailingBookRepository : BookRepository {
        private val error = IllegalStateException("Repository unavailable")

        override fun observeBooks(): Flow<List<Book>> = flow { throw error }
        override fun observeBook(bookId: String): Flow<Book?> = flow { throw error }
        override suspend fun toggleFavorite(bookId: String) = throw error
    }
}
