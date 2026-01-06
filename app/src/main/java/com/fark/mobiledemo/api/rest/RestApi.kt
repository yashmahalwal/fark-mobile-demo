package com.fark.mobiledemo.api.rest

import com.fark.mobiledemo.models.User
import com.fark.mobiledemo.models.Order
import com.fark.mobiledemo.models.Product
import retrofit2.Response
import retrofit2.http.*

interface RestApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<User>
    
    @GET("users")
    suspend fun getUsers(): Response<List<User>>
    
    @POST("users")
    suspend fun createUser(@Body user: CreateUserRequest): Response<CreateUserResponse>
    
    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: CreateUserRequest): Response<UpdateUserResponse>
    
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<DeleteUserResponse>
    
    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: Int): Response<Order>
    
    @GET("orders")
    suspend fun getOrders(): Response<List<Order>>
    
    @POST("orders")
    suspend fun createOrder(@Body order: CreateOrderRequest): Response<CreateOrderResponse>
    
    @PUT("orders/{id}")
    suspend fun updateOrder(@Path("id") id: Int, @Body order: CreateOrderRequest): Response<UpdateOrderResponse>
    
    @DELETE("orders/{id}")
    suspend fun deleteOrder(@Path("id") id: Int): Response<DeleteOrderResponse>
    
    @GET("products")
    suspend fun getProducts(): Response<List<Product>>
    
    @POST("products")
    suspend fun createProduct(@Body product: CreateProductRequest): Response<CreateProductResponse>
    
    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: CreateProductRequest): Response<UpdateProductResponse>
    
    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<DeleteProductResponse>
}

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
