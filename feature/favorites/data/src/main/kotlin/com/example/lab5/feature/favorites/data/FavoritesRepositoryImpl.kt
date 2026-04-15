package com.example.lab5.feature.favorites.data

import com.example.core.store.BookRecord
import com.example.core.store.BookStore
import com.example.lab5.feature.favorites.api.FavoriteBook
import com.example.lab5.feature.favorites.domain.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.dsl.module

class FavoritesRepositoryImpl(private val bookStore: BookStore) : FavoritesRepository {
    override fun observeFavorites(): Flow<List<FavoriteBook>> = bookStore.observeBooks().map { items ->
        items.filter { it.isFavorite }.map(::toFavoriteBook)
    }
    override suspend fun toggleFavorite(bookId: String) = bookStore.toggleFavorite(bookId)

    private fun toFavoriteBook(record: BookRecord): FavoriteBook = FavoriteBook(
        id = record.id,
        title = record.title,
        author = record.author,
        description = record.description,
        genre = record.genre,
        readingStatus = record.readingStatus,
        isFavorite = record.isFavorite
    )
}

val favoritesDataModule = module {
    single<com.example.lab5.feature.favorites.domain.FavoritesRepository> { FavoritesRepositoryImpl(get()) }
}
