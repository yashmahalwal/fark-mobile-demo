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
import com.fark.mobiledemo.models.Order
import com.fark.mobiledemo.ui.components.OrderCard
import com.fark.mobiledemo.ui.dialogs.OrderDialog

@Composable
fun OrdersScreen() {
    var orders by remember { mutableStateOf(listOf<Order>()) }
    var showDialog by remember { mutableStateOf(false) }
    var editingOrder by remember { mutableStateOf<Order?>(null) }
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
                items(orders) { order ->
                    OrderCard(
                        order = order,
                        onEdit = {
                            editingOrder = order
                            showDialog = true
                        },
                        onDelete = {
                            orders = orders.filter { it.id != order.id }
                        }
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

        if (showDialog) {
            OrderDialog(
                order = editingOrder,
                onDismiss = { showDialog = false },
                onSave = { order ->
                    orders = if (editingOrder != null) {
                        orders.map { if (it.id == order.id) order else it }
                    } else {
                        orders + order.copy(id = (orders.maxOfOrNull { it.id } ?: 0) + 1)
                    }
                    showDialog = false
                }
            )
        }
    }
}
