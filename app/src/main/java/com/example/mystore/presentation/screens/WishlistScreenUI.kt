package com.example.mystore.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import coil3.compose.AsyncImage
import com.example.mystore.domain.modelClasses.CartModel
import com.example.mystore.domain.modelClasses.WishListModel
import com.example.mystore.presentation.navigation.OtherScreen
import com.example.mystore.presentation.viewModel.ShopViewModel
import com.example.mystore.presentation.viewModel.UIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreenUI(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: ShopViewModel = hiltViewModel(),
    backStack: NavBackStack
) {
    val wishlistState by viewModel.getWishListState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getWishlistItems()
    }

    Scaffold(
        modifier = Modifier.padding(innerPadding)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (wishlistState) {
                is UIState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UIState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = (wishlistState as UIState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.getWishlistItems() }) {
                            Text("Retry")
                        }
                    }
                }
                is UIState.Success -> {
                    val wishlistItems = (wishlistState as UIState.Success<List<WishListModel>>).data
                    if (wishlistItems.isEmpty()) {
                        EmptyWishlistUI()
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                "My Wishlist (${wishlistItems.size} items)",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(wishlistItems) { wishlistItem ->
                                    WishlistItemCard(
                                        wishlistItem = wishlistItem,
                                        onRemove = { viewModel.removeFromWishList(wishlistItem.id) },
                                        onMoveToCart = {
                                            viewModel.addToCart(
                                                CartModel(
                                                    userId = wishlistItem.userId,
                                                    productId = wishlistItem.productId,
                                                    productName = wishlistItem.productName,
                                                    productImageUrl = wishlistItem.productImageUrl,
                                                    price = wishlistItem.price
                                                )
                                            )
                                            viewModel.removeFromWishList(wishlistItem.id)
                                        },
                                        onClick = {
                                            backStack.add(OtherScreen.ProductDetails(wishlistItem.productId))
                                        }
                                    )
                                }
                                item {
                                    Spacer(modifier = Modifier.height(80.dp))
                                }
                            }
                        }
                    }
                }
                is UIState.Empty -> {
                    EmptyWishlistUI()
                }
            }
        }
    }
}

@Composable
fun WishlistItemCard(
    wishlistItem: WishListModel,
    onRemove: () -> Unit,
    onMoveToCart: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            AsyncImage(
                model = wishlistItem.productImageUrl,
                contentDescription = wishlistItem.productName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Product Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = wishlistItem.productName,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rs. ${wishlistItem.price}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Action Buttons
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onRemove,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove from wishlist"
                    )
                }
                IconButton(
                    onClick = onMoveToCart,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Move to cart"
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyWishlistUI() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Your wishlist is empty",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Add items to your wishlist to save them for later",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
