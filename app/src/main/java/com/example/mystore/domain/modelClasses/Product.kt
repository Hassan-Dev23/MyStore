package com.example.mystore.domain.modelClasses


data class Product(
    var id: String = "",                         // Unique product ID
    val name: String = "",                       // Product name/title
    val description: String = "",                // Full description
    val price: Double = 0.0,                     // Current price
    val originalPrice: Double? = null,           // Price before discount
    val discountPercent: Int? = null,            // % off, if applicable
    val stockQuantity: Int = 0,                  // How many in stock
    val isAvailable: Boolean = false,            // True if in stock and sellable
    val category: String = "",                   // E.g., "Shoes", "Electronics"
    val brand: String = "",                      // E.g., "Samsung", "Nike"
    val imageUrls: List<String>? = emptyList(),  // List of image URLs (carousel)
    val createdAt: String? = System.currentTimeMillis().toString() // Timestamp
)
