package com.fark.mobiledemo.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fark.mobiledemo.ui.screens.Order
import com.fark.mobiledemo.ui.screens.ShippingAddress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDialog(
    order: Order?,
    onDismiss: () -> Unit,
    onSave: (Order) -> Unit
) {
    var userId by remember { mutableStateOf(order?.userId?.toString() ?: "") }
    var productIds by remember { mutableStateOf(order?.productIds?.joinToString(", ") ?: "") }
    var status by remember { mutableStateOf(order?.status ?: "pending") }
    var total by remember { mutableStateOf(order?.total?.toString() ?: "") }
    var discountCode by remember { mutableStateOf(order?.discountCode ?: "") }
    var street by remember { mutableStateOf(order?.shippingAddress?.street ?: "") }
    var city by remember { mutableStateOf(order?.shippingAddress?.city ?: "") }
    var zipCode by remember { mutableStateOf(order?.shippingAddress?.zipCode ?: "") }
    var country by remember { mutableStateOf(order?.shippingAddress?.country ?: "") }
    
    var expandedStatus by remember { mutableStateOf(false) }
    val statuses = listOf("pending", "processing", "shipped", "delivered", "cancelled")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (order == null) "Add Order" else "Edit Order") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it },
                    label = { Text("User ID") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = productIds,
                    onValueChange = { productIds = it },
                    label = { Text("Product IDs (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expandedStatus,
                    onExpandedChange = { expandedStatus = it }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStatus,
                        onDismissRequest = { expandedStatus = false }
                    ) {
                        statuses.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    status = item
                                    expandedStatus = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = total,
                    onValueChange = { total = it },
                    label = { Text("Total") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = discountCode,
                    onValueChange = { discountCode = it },
                    label = { Text("Discount Code") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Shipping Address",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Street") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = zipCode,
                    onValueChange = { zipCode = it },
                    label = { Text("Zip Code") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        Order(
                            id = order?.id ?: 0,
                            userId = userId.toIntOrNull() ?: 0,
                            productIds = productIds.split(",").mapNotNull { it.trim().toIntOrNull() },
                            status = status,
                            total = total.toDoubleOrNull() ?: 0.0,
                            discountCode = discountCode.ifEmpty { null },
                            shippingAddress = ShippingAddress(
                                street = street,
                                city = city,
                                zipCode = zipCode,
                                country = country
                            )
                        )
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
