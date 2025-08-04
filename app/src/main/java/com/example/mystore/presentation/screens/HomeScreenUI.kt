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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import coil3.compose.AsyncImage
import com.example.mystore.presentation.navigation.OtherScreen
import com.example.mystore.presentation.viewModel.CombineUState
import com.example.mystore.presentation.viewModel.ShopViewModel

@Composable
fun HomeScreen(
    viewModel: ShopViewModel = hiltViewModel(),
    paddings: PaddingValues,
    backStack: NavBackStack
) {

    val homeScreenState by viewModel.homeScreenState.collectAsStateWithLifecycle()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(paddings)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar and Notification Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search") },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (homeScreenState is CombineUState.Success) {
            val categoryData = (homeScreenState as CombineUState.Success).data
            val productData = (homeScreenState as CombineUState.Success).data2
            // Categories Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,

                ) {
                Text("Categories", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "See more", color = Color.Red, fontSize = 14.sp, modifier = Modifier
                        .clickable {
                            backStack.add(OtherScreen.AllCategory)
                        }
                        .padding(8.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                items(categoryData) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF5F5F5),
                            modifier = Modifier.size(60.dp).clickable{
                                backStack.add(OtherScreen.ProductsByCategory(it.name))
                            }
                        ) {
                            AsyncImage(
                                model = it.imageUrl,
                                contentDescription = it.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it.name, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Banners Section

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(productData) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(width = 180.dp, height = 220.dp).clickable{
                            backStack.add(OtherScreen.ProductDetails(it.id))
                        },
                        color = Color.LightGray
                    ) {
                        // Placeholder for banner
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model = it.imageUrls?.first(),
                                contentDescription = it.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Flash Sale Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Flash Sale", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("See more", color = Color.Red, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))



            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(productData) { product ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.width(160.dp).clickable{
                            backStack.add(OtherScreen.ProductDetails(product.id))
                        },
                        color = Color.White,
                        tonalElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(180.dp)
                                    .fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = product.imageUrls?.first(),
                                    contentDescription = product.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(product.category, fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    "Rs: ${product.price}",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Rs: ${product.price - (product.price * 0.1)}",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))


        }

        if (homeScreenState is CombineUState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        var showErrorDialog by remember { mutableStateOf(false) }
        if (homeScreenState is CombineUState.Error) {
            LaunchedEffect(Unit) {
                showErrorDialog = true
            }
        }
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = { Text((homeScreenState as CombineUState.Error).message) },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

    }
}

