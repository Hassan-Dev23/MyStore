package com.example.mystore.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import com.example.mystore.presentation.viewModel.ShopViewModel
import com.example.mystore.presentation.viewModel.UIState
import com.example.mystore.domain.modelClasses.Product

@Composable
fun ProductDetailScreenUI(
    innerPadding: PaddingValues,
    productId: String,
    viewModel: ShopViewModel = hiltViewModel(),
    backStack: NavBackStack
) {
    val productState by viewModel.getProductByIdState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.getProductById(productId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(innerPadding)
    ) {
        when (productState) {
            is UIState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is UIState.Error -> {
                Text(
                    text = (productState as UIState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UIState.Success -> {
                val product = (productState as UIState.Success<Product>).data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // Product Images Carousel
                    if (product.imageUrls!!.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(product.imageUrls) {
                                AsyncImage(
                                    model = it,
                                    contentDescription = product.name,
                                    modifier = Modifier
                                        .width(280.dp)
                                        .height(280.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            // Placeholder if no images
                            Text("No Image", color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Product Name and Price
                    Text(product.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Rs: ${product.price}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Product Description
                    Text(product.description, fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Product Brand and Category
                    Text("Brand: ${product.brand}", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Category: ${product.category}", fontSize = 14.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Wishlist and Cart Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { /* Handle share action */ }) {
                            Icon(Icons.Default.FavoriteBorder, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add to Wishlist")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { /* TODO: Add buy now logic */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Buy now")
                        }
                        Button(
                            onClick = { /* TODO: Add to cart logic */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                "Add to Cart",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            else -> {}
        }
    }
}
