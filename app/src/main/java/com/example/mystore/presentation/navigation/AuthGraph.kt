package com.example.mystore.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.mystore.presentation.screens.SignInScreenUI
import com.example.mystore.presentation.screens.SignUpScreenUI

@Composable
fun AuthGraph(onSignInClick: () -> Unit) {
    val backStack = rememberNavBackStack<AuthScreen>(AuthScreen.SignIn)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
         entry<AuthScreen.SignIn>{
             SignInScreenUI(backStack = backStack, onSignInComplete = onSignInClick)
         }
            entry(AuthScreen.SignUp){
                SignUpScreenUI(backStack = backStack, onSignUpComplete = onSignInClick)
            }
        }
    )

}