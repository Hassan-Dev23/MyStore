package com.example.mystore.domain.useCases

import com.example.mystore.domain.modelClasses.WishListModel
import com.example.mystore.domain.repo.Repo
import javax.inject.Inject

class WishListUseCase @Inject constructor(val repo: Repo) {
    /**
     * Use case for managing the wishlist functionality in the application.
     * It provides methods to add, remove, and retrieve products from the wishlist.
     *
     * @property repo The repository instance that interacts with the data source.
     */
    suspend fun addToWishList(wishListProduct : WishListModel) = repo.addProductToWishlist(wishListProduct = wishListProduct)
    suspend fun removeFromWishList(wishListId: String) = repo.removeProductFromWishlist(wishListId = wishListId)
    suspend fun getWishList(userId: String) = repo.getWishlistProducts(userId = userId)
}