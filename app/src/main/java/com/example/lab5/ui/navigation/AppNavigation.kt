package com.example.lab5.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.ui.BookScaffold
import com.example.lab5.AppContainer
import com.example.lab5.ui.about.AboutScreen
import com.example.lab5.ui.auth.LoginScreen
import com.example.lab5.ui.auth.LoginViewModel
import com.example.lab5.ui.books.BookDetailsScreen
import com.example.lab5.ui.books.BooksScreen
import com.example.lab5.ui.books.BooksViewModel

@Composable
fun BookShelfApp() {
    val navController = rememberNavController()
    val viewModel: BooksViewModel = viewModel(factory = AppContainer.booksViewModelFactory)
    val loginViewModel: LoginViewModel = viewModel(factory = AppContainer.loginViewModelFactory)
    val loginState by loginViewModel.state.collectAsStateWithLifecycle()
    val catalogState by viewModel.catalogState.collectAsStateWithLifecycle()
    val favoritesState by viewModel.favoritesState.collectAsStateWithLifecycle()

    if (!loginState.isAuthenticated) {
        LoginScreen(
            state = loginState,
            onLoginClick = loginViewModel::login
        )
        return
    }

    BookScaffold(
        title = "Книжная полка",
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Catalog.route
        ) {
            composable(AppRoute.Catalog.route) {
                LaunchedEffect(Unit) {
                    viewModel.trackScreenViewed("catalog")
                }
                BooksScreen(
                    state = catalogState,
                    onQueryChange = viewModel::onQueryChange,
                    onBookClick = { navController.navigate(AppRoute.Details.build(it)) },
                    onFavoriteToggle = viewModel::toggleFavorite,
                    modifier = androidx.compose.ui.Modifier.padding(innerPadding)
                )
            }
            composable(AppRoute.Favorites.route) {
                LaunchedEffect(Unit) {
                    viewModel.trackScreenViewed("favorites")
                }
                BooksScreen(
                    state = favoritesState,
                    onQueryChange = viewModel::onQueryChange,
                    onBookClick = { navController.navigate(AppRoute.Details.build(it)) },
                    onFavoriteToggle = viewModel::toggleFavorite,
                    modifier = androidx.compose.ui.Modifier.padding(innerPadding)
                )
            }
            composable(AppRoute.About.route) {
                LaunchedEffect(Unit) {
                    viewModel.trackScreenViewed("about")
                }
                AboutScreen(
                    userName = loginState.userName,
                    provider = loginState.provider,
                    login = loginState.login,
                    firstName = loginState.firstName,
                    lastName = loginState.lastName,
                    email = loginState.email,
                    onLogoutClick = loginViewModel::logout,
                    modifier = androidx.compose.ui.Modifier.padding(innerPadding)
                )
            }
            composable(AppRoute.Details.route) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId").orEmpty()
                LaunchedEffect(bookId) {
                    viewModel.trackScreenViewed("book_details")
                }
                val detailsState by viewModel.detailsState(bookId).collectAsStateWithLifecycle()
                BookDetailsScreen(
                    state = detailsState,
                    onBackClick = navController::popBackStack,
                    onFavoriteToggle = viewModel::toggleFavorite,
                    modifier = androidx.compose.ui.Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem(AppRoute.Catalog.route, "Каталог", Icons.AutoMirrored.Outlined.MenuBook),
        NavigationItem(AppRoute.Favorites.route, "Избранное", Icons.Outlined.Bookmarks),
        NavigationItem(AppRoute.About.route, "О нас", Icons.Outlined.Info)
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
