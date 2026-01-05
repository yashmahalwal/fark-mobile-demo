package com.fark.mobiledemo.api.rest

import com.fark.mobiledemo.models.User
import com.fark.mobiledemo.models.Order
import com.fark.mobiledemo.models.Product
import retrofit2.Response
import retrofit2.http.*

interface RestApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<User>
    
    @POST("users")
    suspend fun createUser(@Body user: CreateUserRequest): Response<CreateUserResponse>
    
    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: Int): Response<Order>
    
    @GET("products")
    suspend fun getProducts(): Response<List<Product>>
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

