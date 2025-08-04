package com.example.mystore.domain.modelClasses

data class CartModel(
    var id: String = "",                         // Unique cart ID
    val userId: String = "",                     // User who owns this cart
    val productId: String = "",                  // Product ID in the cart
    val productName: String = "",                // Name of the product
    val productImageUrl: String? = null,         // Image URL of the product
    val quantity: Int = 1,                       // Quantity of the product in the cart
    val price: Double = 0.0,                     // Price of the product
    val addedAt: String = System.currentTimeMillis().toString() // Timestamp when added
)