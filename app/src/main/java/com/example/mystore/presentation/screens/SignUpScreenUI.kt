package com.example.mystore.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import com.example.mystore.domain.modelClasses.UserDetailsModel
import com.example.mystore.presentation.navigation.AuthGraph
import com.example.mystore.presentation.navigation.AuthScreen
import com.example.mystore.presentation.viewModel.ShopViewModel
import com.example.mystore.presentation.viewModel.UIState


@Composable
fun SignUpScreenUI(
    viewModel: ShopViewModel = hiltViewModel(),
    backStack: NavBackStack,
    onSignUpComplete: () -> Unit,
    onSignInClick: () -> Unit = {}
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isButtonEnabled by remember { mutableStateOf(false) }
    val confirmPasswordFocus = remember { mutableStateOf(false) }
    val signUpState by viewModel.signUpState.collectAsStateWithLifecycle()

    fun checkFormValidity(focusLost: Boolean = false) {
        if (password != confirmPassword && confirmPassword.isNotEmpty() && focusLost) {
            isError = true
            errorText = "Passwords do not match"
            isButtonEnabled = false
            return
        }
        isError = false
        errorText = ""
        isButtonEnabled =
            firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword == password
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                "Signup",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Already have an account? ", fontSize = 14.sp)
                Text(
                    "Login",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        backStack.add(AuthScreen.SignIn)
                        backStack.remove(AuthScreen.SignUp)
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it; checkFormValidity()
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = {
                        firstName = it; checkFormValidity()
                    },
                    label = { Text("First Name") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it; checkFormValidity()
                    },
                    label = { Text("Last Name") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it; checkFormValidity()
                },
                label = { Text("Create Password") },
                isError = isError,
                supportingText = { if (isError) Text(errorText) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    isError = false
                    errorText = ""
                    checkFormValidity()
                },
                isError = isError,
                label = { Text("Confirm Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        confirmPasswordFocus.value = focusState.isFocused
                        if (!focusState.isFocused) {
                            checkFormValidity(focusLost = true)
                        }
                    },
                supportingText = { if (isError) Text(errorText) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val user = UserDetailsModel(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        password = password
                    )
                    viewModel.registerUser(user)
                },
                enabled = isButtonEnabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Signup")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("OR", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onSignInClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log in with Facebook")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onSignInClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log in with Google")
            }
        }

        var showSuccessDialog by remember { mutableStateOf(false) }
        var showErrorDialog by remember { mutableStateOf(false) }

        when (signUpState) {
            is UIState.Error -> LaunchedEffect(Unit) {
                showErrorDialog = true; showSuccessDialog = false
            }

            UIState.Loading -> {
                LaunchedEffect(Unit) {
                    showSuccessDialog = false; showErrorDialog = false
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UIState.Success<*> -> LaunchedEffect(Unit) {
                showSuccessDialog = true; showErrorDialog = false
            }
            else -> Unit

        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    onSignUpComplete.invoke()
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Success",
                            tint = Color.Green,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Registration Completed", fontWeight = FontWeight.Bold)
                    }
                },
                text = { Text((signUpState as UIState.Success<*>).data.toString()) },
                confirmButton = {
                    TextButton(onClick = {
                        showSuccessDialog = false
                        onSignUpComplete.invoke()
                    }) {
                        Text("OK")
                    }
                }
            )
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = { Text((signUpState as UIState.Error).message) },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
