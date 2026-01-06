package com.fark.mobiledemo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fark.mobiledemo.ui.components.UserCard
import com.fark.mobiledemo.ui.dialogs.UserDialog

// Placeholder data model
data class User(
    val id: Int,
    val email: String,
    val name: String,
    val status: String,
    val description: String,
    val tags: List<String>,
    val paymentMethod: String
)

@Composable
fun UsersScreen() {
    var users by remember { mutableStateOf(listOf<User>()) }
    var showDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // TODO: Load users from API

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    UserCard(
                        user = user,
                        onEdit = {
                            editingUser = user
                            showDialog = true
                        },
                        onDelete = {
                            // TODO: Delete user
                            users = users.filter { it.id != user.id }
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                editingUser = null
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add User")
        }

        if (showDialog) {
            UserDialog(
                user = editingUser,
                onDismiss = { showDialog = false },
                onSave = { user ->
                    if (editingUser != null) {
                        // Update
                        users = users.map { if (it.id == user.id) user else it }
                    } else {
                        // Add new
                        users = users + user.copy(id = (users.maxOfOrNull { it.id } ?: 0) + 1)
                    }
                    showDialog = false
                }
            )
        }
    }
}
