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
import com.fark.mobiledemo.models.Order
import com.fark.mobiledemo.ui.ApiType
import com.fark.mobiledemo.ui.components.ErrorBanner
import com.fark.mobiledemo.ui.components.OrderCard
import com.fark.mobiledemo.ui.dialogs.OrderDialog
import com.fark.mobiledemo.viewmodels.OrdersViewModel
import com.fark.mobiledemo.viewmodels.RestOrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen() {
    var apiType by remember { mutableStateOf(ApiType.REST) }
    var showDialog by remember { mutableStateOf(false) }
    var editingOrder by remember { mutableStateOf<Order?>(null) }
    
    // Only create ViewModels when needed
    val restViewModel: RestOrdersViewModel? = if (apiType == ApiType.REST) viewModel() else null
    val graphqlViewModel: OrdersViewModel? = if (apiType == ApiType.GRAPHQL) viewModel() else null
    
    // Load data when ViewModel is first created or API type changes
    LaunchedEffect(apiType) {
        when (apiType) {
            ApiType.REST -> restViewModel?.loadOrders()
            ApiType.GRAPHQL -> graphqlViewModel?.loadOrders()
            ApiType.GRPC -> {}
        }
    }
    
    val orders by when (apiType) {
        ApiType.REST -> restViewModel!!.orders.collectAsState()
        ApiType.GRAPHQL -> graphqlViewModel!!.orders.collectAsState()
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
            ApiType.REST -> restViewModel?.loadOrders()
            ApiType.GRAPHQL -> graphqlViewModel?.loadOrders()
            ApiType.GRPC -> {}
        }
    }
    
    val onSave = { order: Order ->
        if (editingOrder != null) {
            when (apiType) {
                ApiType.REST -> restViewModel?.updateOrder(order)
                ApiType.GRAPHQL -> graphqlViewModel?.updateOrder(order)
                ApiType.GRPC -> {}
            }
        } else {
            when (apiType) {
                ApiType.REST -> restViewModel?.createOrder(order)
                ApiType.GRAPHQL -> graphqlViewModel?.createOrder(order)
                ApiType.GRPC -> {}
            }
        }
        showDialog = false
    }
    
    val onDelete = { order: Order ->
        when (apiType) {
            ApiType.REST -> restViewModel?.deleteOrder(order.id)
            ApiType.GRAPHQL -> graphqlViewModel?.deleteOrder(order.id)
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
            if (isLoading && orders.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(orders) { order ->
                        OrderCard(
                            order = order,
                            onEdit = {
                                editingOrder = order
                                showDialog = true
                            },
                            onDelete = { onDelete(order) }
                        )
                    }
                }
            }
            
            FloatingActionButton(
                onClick = {
                    editingOrder = null
                    showDialog = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Order")
            }
            
            if (isLoading && orders.isNotEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
        }
        
        if (showDialog) {
            OrderDialog(
                order = editingOrder,
                onDismiss = { showDialog = false },
                onSave = onSave
            )
        }
    }
}
