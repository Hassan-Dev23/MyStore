package com.example.mystore.domain.useCases

import com.example.mystore.domain.modelClasses.CartModel
import com.example.mystore.domain.repo.Repo
import jakarta.inject.Inject

class CartUseCase @Inject constructor(private val repo: Repo) {
    /**
     * Use case for managing the cart functionality in the application.
     * It provides methods to add, remove, and retrieve products from the cart.
     *
     * @property repo The repository instance that interacts with the data source.
     */
    suspend fun addToCart(cartProduct: CartModel) = repo.addProductToCart(cartProduct = cartProduct)
    suspend fun removeFromCart(cartId: String) = repo.removeProductFromCart(cartId = cartId)
    suspend fun getCart(userId: String) = repo.getCartProducts(userId = userId)
}