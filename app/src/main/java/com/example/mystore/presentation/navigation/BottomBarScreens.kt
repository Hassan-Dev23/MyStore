package com.example.mystore.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.runtime.saveable.Saver
import androidx.navigation3.runtime.NavKey
import com.example.mystore.R
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey{
    @Serializable
    data object Auth : Screen()
    @Serializable
    data object HomeGraph : Screen()
    @Serializable
    data object Setting : Screen()
}


val BottomBarItems = listOf<BottomBarScreens>(
    BottomBarScreens.Home,
    BottomBarScreens.Cart,
    BottomBarScreens.Wishlist,
    BottomBarScreens.Profile

)


@Serializable
sealed class BottomBarScreens(
    val icon :Int ,
    val title : String
) : NavKey {
    @Serializable
    data object Home : BottomBarScreens(
        icon = R.drawable.img,
        title = "Home"
    )
    @Serializable
    data object Cart : BottomBarScreens(
        icon = R.drawable.img_3,
        title = "Cart"
    )
    @Serializable
    data object Wishlist : BottomBarScreens(
        icon = R.drawable.img_4,
        title = "Wishlist"
    )
    @Serializable
    data object Profile : BottomBarScreens(
        icon = R.drawable.img_2,
        title = "Profile"
    )

}

val BottomBarScreenSaver = Saver<BottomBarScreens, String>(
    save = { it::class.simpleName ?: "Unknown"},
    restore = {
        when(it){
            BottomBarScreens.Home::class.simpleName -> BottomBarScreens.Home
            BottomBarScreens.Cart::class.simpleName -> BottomBarScreens.Cart
            BottomBarScreens.Wishlist::class.simpleName -> BottomBarScreens.Wishlist
            BottomBarScreens.Profile::class.simpleName -> BottomBarScreens.Profile
            else -> BottomBarScreens.Home
        }
    }
)



@Serializable
sealed class AuthScreen : NavKey{
    @Serializable
    data object SignIn : AuthScreen()
    @Serializable
    data object SignUp : AuthScreen()

}


@Serializable
sealed class OtherScreen : NavKey {
    @Serializable
    data class ProductDetails(val productId: String) : OtherScreen()
    @Serializable
    data object AllCategory : OtherScreen()
    @Serializable
    data object Search : OtherScreen()
    @Serializable
    data object OrderDetails : OtherScreen()
    @Serializable
    data object OrderHistory : OtherScreen()
    @Serializable
    data class ProductsByCategory(val category : String ) : OtherScreen()

}