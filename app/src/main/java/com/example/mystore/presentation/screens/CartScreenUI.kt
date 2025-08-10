package com.example.mystore.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.mystore.domain.modelClasses.CartModel
import com.example.mystore.presentation.viewModel.ShopViewModel
import com.example.mystore.presentation.viewModel.UIState
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreenUI(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: ShopViewModel = hiltViewModel()
) {
    val cartState by viewModel.getCartListState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.getCartItems()
    }

    Scaffold(
        modifier = Modifier.padding(innerPadding),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (cartState) {
                is UIState.Loading -> {
                    LoadingView()
                }
                is UIState.Error -> {
                    ErrorView(
                        message = (cartState as UIState.Error).message,
                        onRetry = { viewModel.getCartItems() }
                    )
                }
                is UIState.Success -> {
                    val cartItems = (cartState as UIState.Success<List<CartModel>>).data
                    if (cartItems.isEmpty()) {
                        EmptyCartUI()
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Text(
                                "My Cart",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 16.dp,
                                    bottom = 8.dp
                                )
                            )

                            Text(
                                "${cartItems.size} items",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(cartItems) { cartItem ->
                                    CartItemCard(
                                        cartItem = cartItem,
                                        onRemove = { viewModel.removeFromCart(cartItem.id) },
                                        onQuantityChange = { quantity ->
                                            viewModel.updateCartItemQuantity(cartItem.id, quantity)
                                        }
                                    )
                                }
                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                            
                            CartSummary(
                                cartItems = cartItems,
                                onCheckout = { /* TODO: Implement checkout */ }
                            )
                        }
                    }
                }
                else -> Unit

            }
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartModel,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = cartItem.productImageUrl,
                contentDescription = cartItem.productName,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cartItem.productName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${cartItem.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuantitySelector(
                        quantity = cartItem.quantity,
                        onQuantityChange = onQuantityChange
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = onRemove,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(Icons.Default.Delete, "Remove item")
                    }
                }
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilledIconButton(
            onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(Icons.Default.Remove, "Decrease quantity")
        }

        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleMedium
        )

        FilledIconButton(
            onClick = { onQuantityChange(quantity + 1) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(Icons.Default.Add, "Increase quantity")
        }
    }
}

@Composable
private fun CartSummary(
    cartItems: List<CartModel>,
    onCheckout: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    "$${cartItems.sumOf { it.price * it.quantity }}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Proceed to Checkout",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyCartUI() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Your cart is empty",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Add items to your cart to start shopping",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
