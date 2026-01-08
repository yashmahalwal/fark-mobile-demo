package com.fark.mobiledemo.api.rest

// Request/Response data classes for REST API

data class CreateUserRequest(
    val email: String,
    val name: String,
    val status: String,
    val description: String?,
    val metadata: Map<String, Any>?,
    val tags: List<String>,
    val paymentMethod: String
)

data class CreateUserResponse(
    val id: Int
)

data class UpdateUserResponse(
    val id: Int,
    val message: String
)

data class DeleteUserResponse(
    val id: Int,
    val message: String
)

data class CreateOrderRequest(
    val userId: Int,
    val productIds: List<Int>,
    val status: String,
    val total: Double,
    val discountCode: String?,
    val shippingAddress: Map<String, String>
)

data class CreateOrderResponse(
    val id: Int
)

data class UpdateOrderResponse(
    val id: Int,
    val message: String
)

data class DeleteOrderResponse(
    val id: Int,
    val message: String
)

data class CreateProductRequest(
    val name: String,
    val price: Double,
    val category: String,
    val inStock: Boolean,
    val specifications: List<Map<String, String>>
)

data class CreateProductResponse(
    val id: Int
)

data class UpdateProductResponse(
    val id: Int,
    val message: String
)

data class DeleteProductResponse(
    val id: Int,
    val message: String
)
