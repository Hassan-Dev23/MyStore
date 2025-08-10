package com.example.mystore.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.mystore.domain.modelClasses.UserDetailsModel
import com.example.mystore.presentation.viewModel.ShopViewModel
import com.example.mystore.presentation.viewModel.UIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenUI(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: ShopViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val userDetailsState by viewModel.userDetailsState.collectAsStateWithLifecycle()
    val updateProfileState by viewModel.updateProfileState.collectAsStateWithLifecycle()
    val uploadImageState by viewModel.uploadImageState.collectAsStateWithLifecycle()
    var showEditDialog by remember { mutableStateOf(false) }
    var currentUser by remember { mutableStateOf<UserDetailsModel?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadProfileImage(it.toString()) }
    }

    LaunchedEffect(Unit) {
        viewModel.userDetails()
    }

    LaunchedEffect(userDetailsState) {
        if (userDetailsState is UIState.Success) {
            currentUser = (userDetailsState as UIState.Success<UserDetailsModel>).data
        }
    }

    // Handle profile update feedback
    LaunchedEffect(updateProfileState) {
        when (updateProfileState) {
            is UIState.Success -> {
                snackbarHostState.showSnackbar("Profile updated successfully")
                viewModel.userDetails() // Refresh user details
            }
            is UIState.Error -> {
                snackbarHostState.showSnackbar((updateProfileState as UIState.Error).message)
            }
            else -> {}
        }
    }

    // Handle image upload feedback
    LaunchedEffect(uploadImageState) {
        when (uploadImageState) {
            is UIState.Success -> {
                snackbarHostState.showSnackbar("Profile image updated successfully")
                viewModel.userDetails() // Refresh user details
            }
            is UIState.Error -> {
                snackbarHostState.showSnackbar((uploadImageState as UIState.Error).message)
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
                .padding(paddingValues)
        ) {
            when (userDetailsState) {
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
                            text = (userDetailsState as UIState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.userDetails() }) {
                            Text("Retry")
                        }
                    }
                }
                is UIState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Image
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable { imagePickerLauncher.launch("image/*") }
                        ) {
                            if (currentUser?.profileImage?.isNotEmpty() == true) {
                                AsyncImage(
                                    model = currentUser?.profileImage,
                                    contentDescription = "Profile Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = currentUser?.firstName?.firstOrNull()?.toString() ?: "U",
                                    style = MaterialTheme.typography.headlineLarge,
                                    modifier = Modifier.align(Alignment.Center),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            // Edit overlay
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile Image",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // User Name
                        Text(
                            text = "${currentUser?.firstName} ${currentUser?.lastName}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // User Info Cards
                        ProfileInfoCard(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = currentUser?.email ?: "",
                            onClick = { showEditDialog = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileInfoCard(
                            icon = Icons.Default.Phone,
                            label = "Phone",
                            value = currentUser?.phone ?: "",
                            onClick = { showEditDialog = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileInfoCard(
                            icon = Icons.Default.Person,
                            label = "Gender",
                            value = currentUser?.gender ?: "",
                            onClick = { showEditDialog = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileInfoCard(
                            icon = Icons.Default.LocationOn,
                            label = "Address",
                            value = currentUser?.address ?: "",
                            onClick = { showEditDialog = true }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Logout Button
                        Button(
                            onClick = onLogout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Logout")
                        }
                    }
                }
                else -> Unit

            }
        }
    }

    if (showEditDialog && currentUser != null) {
        EditProfileDialog(
            userDetails = currentUser!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedUser ->
                viewModel.updateUserProfile(updatedUser)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun ProfileInfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = value.ifEmpty { "Not set" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value.isEmpty()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                           else MaterialTheme.colorScheme.onSurface
                )
            }

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    userDetails: UserDetailsModel,
    onDismiss: () -> Unit,
    onSave: (UserDetailsModel) -> Unit
) {
    var firstName by remember { mutableStateOf(userDetails.firstName) }
    var lastName by remember { mutableStateOf(userDetails.lastName) }
    var phone by remember { mutableStateOf(userDetails.phone) }
    var gender by remember { mutableStateOf(userDetails.gender) }
    var address by remember { mutableStateOf(userDetails.address) }

    AlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Edit Profile",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = gender,
                    onValueChange = { gender = it },
                    label = { Text("Gender") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                userDetails.copy(
                                    firstName = firstName,
                                    lastName = lastName,
                                    phone = phone,
                                    gender = gender,
                                    address = address
                                )
                            )
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
