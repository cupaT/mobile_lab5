package com.example.lab5

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.ui.BookScaffold
import com.example.lab5.core.navigation.AppDestinations
import com.example.lab5.core.navigation.BottomDestination
import com.example.lab5.feature.catalog.ui.catalogDetailsGraph
import com.example.lab5.feature.catalog.ui.catalogGraph
import com.example.lab5.feature.favorites.ui.favoritesGraph

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val destinations = listOf(
        BottomDestination(AppDestinations.Catalog, "Каталог"),
        BottomDestination(AppDestinations.Favorites, "Избранное")
    )

    BookScaffold(
        title = "Книжная полка",
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination
            NavigationBar {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                if (destination.route == AppDestinations.Catalog) Icons.AutoMirrored.Outlined.MenuBook else Icons.Outlined.Bookmarks,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = AppDestinations.Catalog, modifier = Modifier.padding(innerPadding)) {
            catalogGraph(onOpenDetails = { navController.navigate(AppDestinations.details(it)) })
            catalogDetailsGraph(onBack = navController::popBackStack)
            favoritesGraph(onOpenDetails = { navController.navigate(AppDestinations.details(it)) })
        }
    }
}
