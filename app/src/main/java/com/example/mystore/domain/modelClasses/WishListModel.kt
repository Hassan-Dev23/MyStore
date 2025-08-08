package com.example.mystore.domain.modelClasses

data class WishListModel (
    var id: String = "",                         // Unique wishlist ID
    val userId: String = "",                     // User who owns this wishlist
    val productId: String = "",                  // Product ID in the wishlist
    val productName: String = "",               
    val price : Double = 0.0,                  // Price of the product
    val productImageUrl: String? = null,         // Image URL of the product
    val addedAt: String? = System.currentTimeMillis().toString() // Timestamp when added
)