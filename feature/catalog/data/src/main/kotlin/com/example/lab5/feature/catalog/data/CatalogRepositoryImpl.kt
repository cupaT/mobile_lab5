package com.example.lab5.feature.catalog.data

import com.example.core.store.BookRecord
import com.example.core.store.BookStore
import com.example.lab5.feature.catalog.api.CatalogBook
import com.example.lab5.feature.catalog.domain.CatalogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.dsl.module

class CatalogRepositoryImpl(
    private val bookStore: BookStore
) : CatalogRepository {
    override fun observeBooks(): Flow<List<CatalogBook>> = bookStore.observeBooks().map { items -> items.map(::toCatalogBook) }
    override fun observeBook(bookId: String): Flow<CatalogBook?> = bookStore.observeBook(bookId).map { it?.let(::toCatalogBook) }
    override suspend fun toggleFavorite(bookId: String) = bookStore.toggleFavorite(bookId)

    private fun toCatalogBook(record: BookRecord): CatalogBook = CatalogBook(
        id = record.id,
        title = record.title,
        author = record.author,
        description = record.description,
        genre = record.genre,
        year = record.year,
        readingStatus = record.readingStatus,
        rating = record.rating,
        isFavorite = record.isFavorite
    )
}

val catalogDataModule = module {
    single<com.example.lab5.feature.catalog.domain.CatalogRepository> { CatalogRepositoryImpl(get()) }
}
