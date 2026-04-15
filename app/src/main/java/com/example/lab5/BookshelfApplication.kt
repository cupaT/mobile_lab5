package com.example.lab5

import android.app.Application
import com.example.core.store.BookStore
import com.example.core.store.InMemoryBookStore
import com.example.lab5.feature.catalog.data.catalogDataModule
import com.example.lab5.feature.catalog.ui.catalogUiModule
import com.example.lab5.feature.favorites.data.favoritesDataModule
import com.example.lab5.feature.favorites.ui.favoritesUiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class BookshelfApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BookshelfApplication)
            modules(
                module { single<BookStore> { InMemoryBookStore() } },
                catalogDataModule,
                catalogUiModule,
                favoritesDataModule,
                favoritesUiModule
            )
        }
    }
}
