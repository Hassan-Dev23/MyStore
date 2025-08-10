package com.example.mystore.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import com.example.mystore.presentation.viewModel.ShopViewModel
import com.example.mystore.presentation.viewModel.UIState
import com.example.mystore.domain.modelClasses.Product
import com.example.mystore.domain.modelClasses.WishListModel
import com.example.mystore.domain.modelClasses.CartModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreenUI(
    innerPadding: PaddingValues,
    productId: String,
    viewModel: ShopViewModel = hiltViewModel(),
    backStack: NavBackStack
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val productState by viewModel.getProductByIdState.collectAsState()
    var isInWishlist by remember { mutableStateOf(false) }
    val addToCartState by viewModel.addToCartState.collectAsStateWithLifecycle()

    LaunchedEffect(productId) {
        viewModel.getProductById(productId)
    }

    // UI update for adding/removing from wishlist
    val addToWishlistState by viewModel.addToWishListState.collectAsStateWithLifecycle()
    LaunchedEffect(addToWishlistState) {
        when (addToWishlistState) {
            is UIState.Success -> {
                isInWishlist = true
            }
            else -> {}
        }
    }

    val removeFromWishlistState by viewModel.removeFromWishListState.collectAsStateWithLifecycle()
    LaunchedEffect(removeFromWishlistState) {
        when (removeFromWishlistState) {
            is UIState.Success -> {
                isInWishlist = false
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.padding(innerPadding),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 16.dp, start = 16.dp)
                .padding(paddingValues)
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
                            TextButton(
                                onClick = {
                                    if (!isInWishlist) {
                                        viewModel.addToWishList(
                                            WishListModel(
                                                userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                                productId = product.id,
                                                productName = product.name,
                                                productImageUrl = product.imageUrls.firstOrNull(),
                                                price = product.price,
                                                addedAt = System.currentTimeMillis().toString()
                                            )
                                        )
                                    } else {
                                        // Remove from wishlist logic here
                                        viewModel.removeFromWishList(product.id)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isInWishlist) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (isInWishlist) "Remove from Wishlist" else "Add to Wishlist",
                                    tint = if (isInWishlist) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isInWishlist) "Added to Wishlist" else "Add to Wishlist")
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
                                onClick = {
                                    viewModel.addToCart(
                                        CartModel(
                                            userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                            productId = product.id,
                                            productName = product.name,
                                            productImageUrl = product.imageUrls.firstOrNull(),
                                            price = product.price,
                                            addedAt = System.currentTimeMillis().toString()
                                        )
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                when (addToCartState) {
                                    is UIState.Loading -> {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                    else -> {
                                        Text(
                                            "Add to Cart",
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }

    // Add LaunchedEffect for Cart feedback
    LaunchedEffect(addToCartState) {
        when (addToCartState) {
            is UIState.Success -> {
                snackbarHostState.showSnackbar("Added to cart successfully!")
            }
            is UIState.Error -> {
                snackbarHostState.showSnackbar(
                    (addToCartState as UIState.Error).message,
                    withDismissAction = true
                )
            }
            else -> {}
        }
    }

    // Add LaunchedEffect for Wishlist feedback
    LaunchedEffect(addToWishlistState) {
        when (addToWishlistState) {
            is UIState.Success -> {
                snackbarHostState.showSnackbar("Added to wishlist!")
            }
            is UIState.Error -> {
                snackbarHostState.showSnackbar(
                    (addToWishlistState as UIState.Error).message,
                    withDismissAction = true
                )
            }
            else -> {}
        }
    }
}
