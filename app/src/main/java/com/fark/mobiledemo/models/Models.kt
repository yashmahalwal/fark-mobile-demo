package com.fark.mobiledemo.models

// Shared models matching backend API
enum class UserStatus {
    ACTIVE,
    INACTIVE,
    PENDING
}

enum class OrderStatus {
    CREATED,
    PROCESSING,
    SHIPPED,
    DELIVERED
}

enum class PaymentMethod {
    CREDIT_CARD,
    DEBIT_CARD,
    PAYPAL
}

data class User(
    val id: Int,
    val email: String,
    val name: String,
    val status: UserStatus,
    val description: String?, // nullable field
    val metadata: Map<String, Any>?,
    val tags: List<String>, // array structure
    val paymentMethod: PaymentMethod
)

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val inStock: Boolean,
    val specifications: List<ProductSpec>
)

data class ProductSpec(
    val key: String,
    val value: String
)

data class Order(
    val id: Int,
    val userId: Int,
    val productIds: List<Int>, // array structure
    val status: OrderStatus,
    val total: Double,
    val discountCode: String?, // nullable
    val shippingAddress: Address // nested object
)

data class Address(
    val street: String,
    val city: String,
    val zipCode: String,
    val country: String
)

