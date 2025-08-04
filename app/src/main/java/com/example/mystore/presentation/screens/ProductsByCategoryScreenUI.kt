package com.example.mystore.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.mystore.domain.modelClasses.Product
import com.example.mystore.presentation.viewModel.ShopViewModel
import com.example.mystore.presentation.viewModel.UIState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductsByCategoryScreenUI(
    category: String,
    paddings: PaddingValues ,
    viewModel: ShopViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val productsState by viewModel.getProductsByCategoryState.collectAsStateWithLifecycle()

    LaunchedEffect(category) {
        viewModel.getProductsByCategory(category)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(paddings)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search products") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        val title = when (productsState) {
            is UIState.Success<*> -> {
                val products = (productsState as UIState.Success<List<Product>>).data
                if (products.isNotEmpty()) "Products in \"${products.first().category}\"" else "Products"
            }
            else -> "Products"
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        when (productsState) {
            is UIState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is UIState.Error -> {
                Text(
                    text = (productsState as UIState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            is UIState.Success<*> -> {
                val products = (productsState as UIState.Success<List<Product>>).data
                if (products.isEmpty()) {
                    Text("No products found.", modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(products.filter { it.name.contains(searchQuery, ignoreCase = true) }) { product ->
                            ProductCardTemuStyle(product)
                        }
                    }
                }
            }
            is UIState.Empty -> {}
        }
    }
}

@Composable
fun ProductCardTemuStyle(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = product.imageUrls?.firstOrNull(),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Rs: ${product.price}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp
                )
                if (product.originalPrice != null && product.originalPrice > product.price) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Rs: ${product.originalPrice}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        style = LocalTextStyle.current.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                    )
                }
                if (product.discountPercent != null && product.discountPercent > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "-${product.discountPercent}%",
                        fontSize = 12.sp,
                        color = Color(0xFFE57373),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.brand,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
