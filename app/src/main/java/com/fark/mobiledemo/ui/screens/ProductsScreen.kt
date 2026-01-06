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
import com.fark.mobiledemo.ui.components.ProductCard
import com.fark.mobiledemo.ui.dialogs.ProductDialog

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val inStock: Boolean
)

@Composable
fun ProductsScreen() {
    var products by remember { mutableStateOf(listOf<Product>()) }
    var showDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var isLoading by remember { mutableStateOf(false) }

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
                items(products) { product ->
                    ProductCard(
                        product = product,
                        onEdit = {
                            editingProduct = product
                            showDialog = true
                        },
                        onDelete = {
                            products = products.filter { it.id != product.id }
                        }
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

        if (showDialog) {
            ProductDialog(
                product = editingProduct,
                onDismiss = { showDialog = false },
                onSave = { product ->
                    if (editingProduct != null) {
                        products = products.map { if (it.id == product.id) product else it }
                    } else {
                        products = products + product.copy(id = (products.maxOfOrNull { it.id } ?: 0) + 1)
                    }
                    showDialog = false
                }
            )
        }
    }
}
