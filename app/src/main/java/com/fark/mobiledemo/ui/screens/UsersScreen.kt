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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fark.mobiledemo.models.User
import com.fark.mobiledemo.ui.ApiType
import com.fark.mobiledemo.ui.components.ErrorBanner
import com.fark.mobiledemo.ui.components.UserCard
import com.fark.mobiledemo.ui.dialogs.UserDialog
import com.fark.mobiledemo.viewmodels.RestUsersViewModel
import com.fark.mobiledemo.viewmodels.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen() {
    var apiType by remember { mutableStateOf(ApiType.REST) }
    var showDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<User?>(null) }
    
    // Only create ViewModels when needed
    val restViewModel: RestUsersViewModel? = if (apiType == ApiType.REST) viewModel() else null
    val graphqlViewModel: UsersViewModel? = if (apiType == ApiType.GRAPHQL) viewModel() else null
    
    // Load data when ViewModel is first created or API type changes
    LaunchedEffect(apiType) {
        when (apiType) {
            ApiType.REST -> restViewModel?.loadUsers()
            ApiType.GRAPHQL -> graphqlViewModel?.loadUsers()
            ApiType.GRPC -> {}
        }
    }
    
    val users by when (apiType) {
        ApiType.REST -> restViewModel!!.users.collectAsState()
        ApiType.GRAPHQL -> graphqlViewModel!!.users.collectAsState()
        ApiType.GRPC -> remember { mutableStateOf(emptyList()) }
    }
    
    val isLoading by when (apiType) {
        ApiType.REST -> restViewModel!!.isLoading.collectAsState()
        ApiType.GRAPHQL -> graphqlViewModel!!.isLoading.collectAsState()
        ApiType.GRPC -> remember { mutableStateOf(false) }
    }
    
    val error by when (apiType) {
        ApiType.REST -> restViewModel!!.error.collectAsState()
        ApiType.GRAPHQL -> graphqlViewModel!!.error.collectAsState()
        ApiType.GRPC -> remember { mutableStateOf(null) }
    }
    
    val onRefresh = {
        when (apiType) {
            ApiType.REST -> restViewModel?.loadUsers()
            ApiType.GRAPHQL -> graphqlViewModel?.loadUsers()
            ApiType.GRPC -> {}
        }
    }
    
    val onSave = { user: User ->
        if (editingUser != null) {
            when (apiType) {
                ApiType.REST -> restViewModel?.updateUser(user)
                ApiType.GRAPHQL -> graphqlViewModel?.updateUser(user)
                ApiType.GRPC -> {}
            }
        } else {
            when (apiType) {
                ApiType.REST -> restViewModel?.createUser(user)
                ApiType.GRAPHQL -> graphqlViewModel?.createUser(user)
                ApiType.GRPC -> {}
            }
        }
        showDialog = false
    }
    
    val onDelete = { user: User ->
        when (apiType) {
            ApiType.REST -> restViewModel?.deleteUser(user.id)
            ApiType.GRAPHQL -> graphqlViewModel?.deleteUser(user.id)
            ApiType.GRPC -> {}
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // API Type Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ApiType.values().forEach { type ->
                FilterChip(
                    selected = apiType == type,
                    onClick = { 
                        apiType = type
                        onRefresh()
                    },
                    label = { Text(type.displayName) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Error Banner
        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            ErrorBanner(
                message = it,
                onDismiss = {
                    when (apiType) {
                        ApiType.REST -> restViewModel?.clearError()
                        ApiType.GRAPHQL -> graphqlViewModel?.clearError()
                        ApiType.GRPC -> {}
                    }
                }
            )
        }
        
        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading && users.isEmpty()) {
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
                            onDelete = { onDelete(user) }
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
            
            if (isLoading && users.isNotEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
        }
        
        if (showDialog) {
            UserDialog(
                user = editingUser,
                onDismiss = { showDialog = false },
                onSave = onSave
            )
        }
    }
}
