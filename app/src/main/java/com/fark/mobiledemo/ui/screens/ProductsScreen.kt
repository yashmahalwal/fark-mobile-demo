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
import com.fark.mobiledemo.models.Product
import com.fark.mobiledemo.ui.ApiType
import com.fark.mobiledemo.ui.components.ErrorBanner
import com.fark.mobiledemo.ui.components.ProductCard
import com.fark.mobiledemo.ui.dialogs.ProductDialog
import com.fark.mobiledemo.viewmodels.ProductsViewModel
import com.fark.mobiledemo.viewmodels.RestProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen() {
    var apiType by remember { mutableStateOf(ApiType.REST) }
    var showDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    
    // Only create ViewModels when needed
    val restViewModel: RestProductsViewModel? = if (apiType == ApiType.REST) viewModel() else null
    val graphqlViewModel: ProductsViewModel? = if (apiType == ApiType.GRAPHQL) viewModel() else null
    
    // Load data when ViewModel is first created or API type changes
    LaunchedEffect(apiType) {
        when (apiType) {
            ApiType.REST -> restViewModel?.loadProducts()
            ApiType.GRAPHQL -> graphqlViewModel?.loadProducts()
            ApiType.GRPC -> {}
        }
    }
    
    val products by when (apiType) {
        ApiType.REST -> restViewModel!!.products.collectAsState()
        ApiType.GRAPHQL -> graphqlViewModel!!.products.collectAsState()
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
            ApiType.REST -> restViewModel?.loadProducts()
            ApiType.GRAPHQL -> graphqlViewModel?.loadProducts()
            ApiType.GRPC -> {}
        }
    }
    
    val onSave = { product: Product ->
        if (editingProduct != null) {
            when (apiType) {
                ApiType.REST -> restViewModel?.updateProduct(product)
                ApiType.GRAPHQL -> graphqlViewModel?.updateProduct(product)
                ApiType.GRPC -> {}
            }
        } else {
            when (apiType) {
                ApiType.REST -> restViewModel?.createProduct(product)
                ApiType.GRAPHQL -> graphqlViewModel?.createProduct(product)
                ApiType.GRPC -> {}
            }
        }
        showDialog = false
    }
    
    val onDelete = { product: Product ->
        when (apiType) {
            ApiType.REST -> restViewModel?.deleteProduct(product.id)
            ApiType.GRAPHQL -> graphqlViewModel?.deleteProduct(product.id)
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
            if (isLoading && products.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products) { product ->
                        ProductCard(
                            product = product,
                            onEdit = {
                                editingProduct = product
                                showDialog = true
                            },
                            onDelete = { onDelete(product) }
                        )
                    }
                }
            }
            
            FloatingActionButton(
                onClick = {
                    editingProduct = null
                    showDialog = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
            
            if (isLoading && products.isNotEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
        }
        
        if (showDialog) {
            ProductDialog(
                product = editingProduct,
                onDismiss = { showDialog = false },
                onSave = onSave
            )
        }
    }
}
