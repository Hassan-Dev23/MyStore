package com.example.mystore.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.mystore.presentation.screens.*
import com.example.mystore.presentation.viewModel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NestedGraph(
    viewModel: ShopViewModel = hiltViewModel()
) {
    val backStack = rememberNavBackStack(BottomBarScreens.Home)
    val currentScreen = backStack.last()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                BottomBarItems.forEach { screen ->
                    val selected = currentScreen == screen
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (currentScreen != screen) {
                                backStack.removeLastOrNull()
                                backStack.add(screen)
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = screen.icon),
                                contentDescription = screen.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        // Only apply bottom padding from Scaffold, let each screen handle its own top padding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            NavDisplay(
                modifier = Modifier.fillMaxSize(),
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSavedStateNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    // Implement bottom nav screens
                    entry<BottomBarScreens.Home> {
                        HomeScreen(
                            innerPadding = PaddingValues(0.dp),  // No additional padding needed
                            backStack = backStack
                        )
                    }
                    entry<BottomBarScreens.Cart> {
                        CartScreenUI(
                            innerPadding = PaddingValues(0.dp)  // No additional padding needed
                        )
                    }
                    entry<BottomBarScreens.Profile> {
                        ProfileScreenUI(
                            innerPadding = PaddingValues(0.dp),  // No additional padding needed
                            onLogout = {
                                viewModel.logout()
                            }
                        )
                    }
                    entry<BottomBarScreens.Wishlist> {
                        WishlistScreenUI(
                            innerPadding = PaddingValues(0.dp),  // No additional padding needed
                            backStack = backStack
                        )
                    }

                    // Other screens
                    entry<OtherScreen.AllProducts> {
                        AllProductsScreenUI(
                            search = it.searchQuery,
                            paddings = PaddingValues(0.dp),  // No additional padding needed
                            backStack = backStack
                        )
                    }
                    entry<OtherScreen.AllCategory> {
                        AllCategoryScreenUI(
                            PaddingValues(0.dp),  // No additional padding needed
                            backStack = backStack
                        )
                    }
                    entry<OtherScreen.ProductsByCategory> {
                        ProductsByCategoryScreenUI(
                            it.category,
                            backStack = backStack,
                            paddings = PaddingValues(0.dp)  // No additional padding needed
                        )
                    }
                    entry<OtherScreen.ProductDetails> {
                        ProductDetailScreenUI(
                            innerPadding = PaddingValues(0.dp),  // No additional padding needed
                            productId = it.productId,
                            backStack = backStack
                        )
                    }
                }
            )
        }
    }

}