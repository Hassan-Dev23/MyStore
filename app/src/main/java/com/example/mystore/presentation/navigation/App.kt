package com.example.mystore.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

@Composable
fun App() {

    val backStack = rememberNavBackStack(Screen.Auth)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Screen.Auth> {
                AuthGraph(onSignInClick = {
                    backStack.removeLastOrNull()
                    backStack.add(Screen.HomeGraph)
                })
            }
            entry<Screen.HomeGraph> {
                NestedGraph(onSettingClick = {
                    backStack.add(Screen.Setting)
                })
            }
            entry<Screen.Setting> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("This is Settings Screen!")
                }
                Spacer(Modifier.height(10.dp))
                Button(onClick = {
                    backStack.removeLastOrNull()
                }) {
                    Text("Back")
                }

            }
        }
    )
}