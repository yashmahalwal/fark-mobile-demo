package com.fark.mobiledemo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.fark.mobiledemo.ui.screens.OrdersScreen
import com.fark.mobiledemo.ui.screens.ProductsScreen
import com.fark.mobiledemo.ui.screens.UsersScreen

enum class AppScreen(val title: String) {
    Users("Users"),
    Products("Products"),
    Orders("Orders")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarkMobileDemoApp() {
    var selectedScreen by remember { mutableStateOf(AppScreen.Users) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fark Mobile Demo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedScreen.ordinal) {
                AppScreen.values().forEach { screen ->
                    Tab(
                        selected = selectedScreen == screen,
                        onClick = { selectedScreen = screen },
                        text = { Text(screen.title) }
                    )
                }
            }

            when (selectedScreen) {
                AppScreen.Users -> UsersScreen()
                AppScreen.Products -> ProductsScreen()
                AppScreen.Orders -> OrdersScreen()
            }
        }
    }
}
