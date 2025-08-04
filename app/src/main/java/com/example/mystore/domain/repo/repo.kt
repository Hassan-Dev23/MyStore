package com.example.mystore.domain.repo

import android.net.wifi.hotspot2.pps.Credential
import com.example.mystore.common.ResultState
import com.example.mystore.domain.modelClasses.CartModel
import com.example.mystore.domain.modelClasses.CategoryModel
import com.example.mystore.domain.modelClasses.Product
import com.example.mystore.domain.modelClasses.UserCredentialsModel
import com.example.mystore.domain.modelClasses.UserDetailsModel
import com.example.mystore.domain.modelClasses.WishListModel
import kotlinx.coroutines.flow.Flow

interface Repo {
    suspend fun registerUserWithEmailAndPassword(user: UserDetailsModel): Flow<ResultState<String>>
    suspend fun loginUserWithEmailAndPassword(user: UserCredentialsModel): Flow<ResultState<String>>
    suspend fun getAllCategories(): Flow<ResultState<List<CategoryModel>>>
    suspend fun getAllProducts(): Flow<ResultState<List<Product>>>
    suspend fun getProductsByCategory(category: String): Flow<ResultState<List<Product>>>
    suspend fun getProductById(productId: String): Flow<ResultState<Product>>
    suspend fun getHomeCategories(): Flow<ResultState<List<CategoryModel>>>
    suspend fun addProductToCart(cartProduct: CartModel): Flow<ResultState<String>>
    suspend fun getCartProducts(userId: String): Flow<ResultState<List<CartModel>>>
    suspend fun removeProductFromCart(cartId: String): Flow<ResultState<String>>
    suspend fun addProductToWishlist(wishListProduct: WishListModel): Flow<ResultState<String>>
    suspend fun getWishlistProducts(userId: String): Flow<ResultState<List<WishListModel>>>
    suspend fun removeProductFromWishlist(wishListId: String): Flow<ResultState<String>>


}