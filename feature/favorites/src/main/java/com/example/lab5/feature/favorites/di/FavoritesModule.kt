package com.example.lab5.feature.favorites.di

import com.example.lab5.feature.favorites.domain.GetFavoriteBooksUseCase
import com.example.lab5.feature.favorites.domain.ToggleFavoriteUseCase
import com.example.lab5.feature.favorites.ui.FavoritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val favoritesFeatureModule = module {
    factory { GetFavoriteBooksUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
    viewModel { FavoritesViewModel(get(), get()) }
}
