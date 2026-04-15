package com.example.lab5.core.navigation

object AppDestinations {
    const val Catalog = "catalog"
    const val Favorites = "favorites"
    const val Details = "details/{bookId}"

    fun details(bookId: String): String = "details/$bookId"
}
