package com.example.mystore.presentation.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.mystore.presentation.screens.AllCategoryScreenUI
import com.example.mystore.presentation.screens.AllProductsScreenUI
import com.example.mystore.presentation.screens.CartScreenUI
import com.example.mystore.presentation.screens.HomeScreen
import com.example.mystore.presentation.screens.ProductDetailScreenUI
import com.example.mystore.presentation.screens.ProductsByCategoryScreenUI
import com.example.mystore.presentation.screens.ProfileScreenUI
import com.example.mystore.presentation.screens.WishlistScreenUI
import com.example.mystore.presentation.viewModel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NestedGraph(viewModel: ShopViewModel = hiltViewModel(), onSettingClick: () -> Unit) {
    val backStack = rememberNavBackStack<BottomBarScreens>(BottomBarScreens.Home)

    var currentBottomBarScreen: BottomBarScreens by rememberSaveable(
        stateSaver = BottomBarScreenSaver
    ) { mutableStateOf(BottomBarScreens.Home) }

    Scaffold(

        bottomBar = {
            NavigationBar(
                // Apply the height modifier here to the NavigationBar itself
                modifier = Modifier.height(72.dp) // You can adjust this value as needed
            ) {
                BottomBarItems.forEach { destination ->
                    NavigationBarItem(
                        selected = currentBottomBarScreen == destination,
                        icon = {
                            Icon(
                                painterResource(destination.icon),
                                contentDescription = destination.title
                            )
                        },
                        onClick = {
                            if (backStack.lastOrNull() != destination) {
                                if (backStack.lastOrNull() in BottomBarItems) {
                                    backStack.removeAt(backStack.lastIndex)
                                }
                                backStack.add(destination)
                                currentBottomBarScreen = destination
                            }
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)


                    )
                }
            }

        }
    ) { paddingValues ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<BottomBarScreens.Home> {
                    HomeScreen(innerPadding = paddingValues, backStack = backStack)
                }
                entry<BottomBarScreens.Cart> {
                    CartScreenUI(
                        innerPadding = paddingValues
                    )
                }
                entry<BottomBarScreens.Profile> {
                    ProfileScreenUI(
                        innerPadding = paddingValues,
                        onLogout = {
                            viewModel.logout()
                        }
                    )
                }
                entry<BottomBarScreens.Wishlist> {
                    WishlistScreenUI(
                        innerPadding = paddingValues,
                        backStack = backStack
                    )
                }
                entry<OtherScreen.AllProducts> {
                    AllProductsScreenUI(
                        search = it.searchQuery,
                        paddings = paddingValues,
                        backStack = backStack
                    )
                }
                entry<OtherScreen.AllCategory> {
                    AllCategoryScreenUI(
                        paddingValues,
                        backStack = backStack
                    )
                }
                entry<OtherScreen.ProductsByCategory> {
                    ProductsByCategoryScreenUI(
                        it.category,
                        backStack = backStack,
                        paddings = paddingValues
                    )
                }
                entry<OtherScreen.ProductDetails> {
                    ProductDetailScreenUI(
                        innerPadding = paddingValues, productId = it.productId,
                        backStack = backStack
                    )
                }

            }
        )

    }

}