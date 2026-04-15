package com.example.lab5

import android.app.Application
import com.example.core.books.BookRepository
import com.example.lab5.feature.catalog.data.InMemoryBookRepository
import com.example.lab5.feature.catalog.di.catalogFeatureModule
import com.example.lab5.feature.favorites.di.favoritesFeatureModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class BookshelfApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BookshelfApplication)
            modules(
                module {
                    single<BookRepository> { InMemoryBookRepository() }
                },
                catalogFeatureModule,
                favoritesFeatureModule
            )
        }
    }
}
