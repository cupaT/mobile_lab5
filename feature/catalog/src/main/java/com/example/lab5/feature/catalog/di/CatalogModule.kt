package com.example.lab5.feature.catalog.di

import com.example.lab5.feature.catalog.domain.GetBookDetailsUseCase
import com.example.lab5.feature.catalog.domain.GetBooksUseCase
import com.example.lab5.feature.catalog.domain.SearchBooksUseCase
import com.example.lab5.feature.catalog.domain.ToggleFavoriteUseCase
import com.example.lab5.feature.catalog.ui.CatalogViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val catalogFeatureModule = module {
    factory { GetBooksUseCase(get()) }
    factory { GetBookDetailsUseCase(get()) }
    factory { SearchBooksUseCase() }
    factory { ToggleFavoriteUseCase(get()) }
    viewModel { CatalogViewModel(get(), get(), get(), get()) }
}
