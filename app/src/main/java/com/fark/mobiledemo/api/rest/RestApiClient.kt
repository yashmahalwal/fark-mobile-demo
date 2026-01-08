package com.fark.mobiledemo.api.rest

import android.util.Log
import com.fark.mobiledemo.BuildConfig
import com.fark.mobiledemo.models.*
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*

class RestApiClient {
    private val baseUrl = BuildConfig.REST_API_BASE_URL.trimEnd('/')
    private val gson = Gson()
    
    init {
        Log.d("RestApiClient", "Using base URL: $baseUrl")
    }
    
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            gson()
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("RestApiClient", message)
                }
            }
            level = LogLevel.ALL
        }
    }
    
    suspend fun getUsers(): Result<List<User>> {
        return try {
            Log.d("RestApiClient", "GET $baseUrl/users")
            val response: HttpResponse = client.get("$baseUrl/users")
            
            if (response.status.isSuccess()) {
                val json = response.bodyAsText()
                Log.d("RestApiClient", "Response: $json")
                val usersArray = gson.fromJson(json, Array<User>::class.java)
                Result.success(usersArray.toList())
            } else {
                Result.failure(Exception("Failed to fetch users: ${response.status}"))
            }
        } catch (e: Exception) {
            Log.e("RestApiClient", "Error fetching users", e)
            Result.failure(e)
        }
    }
    
    suspend fun createUser(user: User): Result<CreateUserResponse> {
        return try {
            val request = CreateUserRequest(
                email = user.email,
                name = user.name,
                status = user.status.name,
                description = user.description,
                metadata = user.metadata,
                tags = user.tags,
                paymentMethod = "CREDIT_CARD" // Default value
            )
            
            val json = gson.toJson(request)
            val response: HttpResponse = client.post("$baseUrl/users") {
                contentType(ContentType.Application.Json)
                setBody(json)
            }
            
            if (response.status.isSuccess()) {
                val json = response.bodyAsText()
                val result = gson.fromJson(json, CreateUserResponse::class.java)
                Result.success(result)
            } else {
                Result.failure(Exception("Failed to create user: ${response.status} - ${response.bodyAsText()}"))
            }
        } catch (e: Exception) {
            Log.e("RestApiClient", "Error creating user", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateUser(id: Int, user: User): Result<UpdateUserResponse> {
        return try {
            val request = CreateUserRequest(
                email = user.email,
                name = user.name,
                status = user.status.name,
                description = user.description,
                metadata = user.metadata,
                tags = user.tags,
                paymentMethod = "CREDIT_CARD" // Default value
            )
            
            val json = gson.toJson(request)
            val response: HttpResponse = client.put("$baseUrl/users/$id") {
                contentType(ContentType.Application.Json)
                setBody(json)
            }
            
            if (response.status.isSuccess()) {
                val json = response.bodyAsText()
                val result = gson.fromJson(json, UpdateUserResponse::class.java)
                Result.success(result)
            } else {
                Result.failure(Exception("Failed to update user: ${response.status} - ${response.bodyAsText()}"))
            }
        } catch (e: Exception) {
            Log.e("RestApiClient", "Error updating user", e)
            Result.failure(e)
        }
    }
    
    suspend fun deleteUser(id: Int): Result<DeleteUserResponse> {
        return try {
            val response: HttpResponse = client.delete("$baseUrl/users/$id")
            
            if (response.status.isSuccess()) {
                val json = response.bodyAsText()
                val result = gson.fromJson(json, DeleteUserResponse::class.java)
                Result.success(result)
            } else {
                Result.failure(Exception("Failed to delete user: ${response.status} - ${response.bodyAsText()}"))
            }
        } catch (e: Exception) {
            Log.e("RestApiClient", "Error deleting user", e)
            Result.failure(e)
        }
    }
    
    suspend fun getOrders(): Result<List<Order>> {
        return try {
            val response: HttpResponse = client.get("$baseUrl/orders")
            
            if (response.status.isSuccess()) {
                val json = response.bodyAsText()
                val orders = gson.fromJson(json, Array<Order>::class.java).toList()
                Result.success(orders)
            } else {
                Result.failure(Exception("Failed to fetch orders: ${response.status}"))
            }
        } catch (e: Exception) {
            Log.e("RestApiClient", "Error fetching orders", e)
            Result.failure(e)
        }
    }
    
    suspend fun createOrder(order: Order): Result<CreateOrderResponse> {
        return try {
            val request = CreateOrderRequest(
                userId = order.userId,
                productIds = order.productIds,
                status = order.status.name,
                total = order.total,
                discountCode = order.discountCode,
                shippingAddress = mapOf(
                    "street" to order.shippingAddress.street,
                    "city" to order.shippingAddress.city,
                    "zipCode" to order.shippingAddress.zipCode,
                    "country" to order.shippingAddress.country
                )
            )
            
            val json = gson.toJson(request)
            val response: HttpResponse = client.post("$baseUrl/orders") {
                contentType(ContentType.Application.Json)
                setBody(json)
            }
            
            if (response.status.isSuccess()) {
                val responseJson = response.bodyAsText()
                val result = gson.fromJson(responseJson, CreateOrderResponse::class.java)
                Result.success(result)
            } else {
                Result.failure(Exception("Failed to create order: ${response.status}"))
            }
        } catch (e: Exception) {
            Log.e("RestApiClient", "Error creating order", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateOrder(id: Int, order: Order): Result<UpdateOrderResponse> {
        return try {
            val request = CreateOrderRequest(
                userId = order.userId,
                productIds = order.productIds,
                status = order.status.name,
                total = order.total,
                discountCode = order.discountCode,
                shippingAddress = mapOf(
                    "street" to order.shippingAddress.street,
                    "city" to order.shippingAddress.city,
                    "zipCode" to order.shippingAddress.zipCode,
                    "country" to order.shippingAddress.country
                )
            )
            
            val json = gson.toJson(request)
            val response: HttpResponse = client.put("$baseUrl/orders/$id") {
                contentType(ContentType.Application.Json)
                setBody(json)
            }
            
            if (response.status.isSuccess()) {
                val responseJson = response.bodyAsText()
                val result = gson.fromJson(responseJson, UpdateOrderResponse::class.java)
                Result.success(result)
            } else {
                Result.failure(Exception("Failed to update order: ${response.status}"))
            }
        } catch (e: Exception) {
            Log.e("RestApiClient", "Error updating order", e)
            Result.failure(e)
        }
    }
    
    suspend fun deleteOrder(id: Int): Result<DeleteOrderResponse> {
        return try {
            val response: HttpResponse = client.delete("$baseUrl/orders/$id")
            
            if (response.status.isSuccess()) {
                val json = response.bodyAsText()
                val result = gson.fromJson(json, DeleteOrderResponse::class.java)
                Result.success(result)
            } else {
                Result.failure(Exception("Failed to delete order: ${response.status}"))
            }
        } catch (e: Exception) {
            Log.e("RestApiClient", "Error deleting order", e)
            Result.failure(e)
        }
    }
    
    suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response: HttpResponse = client.get("$baseUrl/products")
            
            if (response.status.isSuccess()) {
                val json = response.bodyAsText()
                val products = gson.fromJson(json, Array<Product>::class.java).toList()
                Result.success(products)
            } else {
                Result.failure(Exception("Failed to fetch products: ${response.status}"))
            }
        } catch (e: Exception) {
            Log.e("RestApiClient", "Error fetching products", e)
            Result.failure(e)
        }
    }
    
    suspend fun createProduct(product: Product): Result<CreateProductResponse> {
        return try {
            val request = CreateProductRequest(
                name = product.name,
                price = product.price,
                category = product.category,
                inStock = product.inStock,
                specifications = product.specifications.map { 
                    mapOf("key" to it.key, "value" to it.value) 
                }
            )
            
            val json = gson.toJson(request)
            val response: HttpResponse = client.post("$baseUrl/products") {
                contentType(ContentType.Application.Json)
                setBody(json)
            }
            
            if (response.status.isSuccess()) {
                val responseJson = response.bodyAsText()
                val result = gson.fromJson(responseJson, CreateProductResponse::class.java)
                Result.success(result)
            } else {
                Result.failure(Exception("Failed to create product: ${response.status}"))
            }
        } catch (e: Exception) {
            Log.e("RestApiClient", "Error creating product", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateProduct(id: Int, product: Product): Result<UpdateProductResponse> {
        return try {
            val request = CreateProductRequest(
                name = product.name,
                price = product.price,
                category = product.category,
                inStock = product.inStock,
                specifications = product.specifications.map { 
                    mapOf("key" to it.key, "value" to it.value) 
                }
            )
            
            val json = gson.toJson(request)
            val response: HttpResponse = client.put("$baseUrl/products/$id") {
                contentType(ContentType.Application.Json)
                setBody(json)
            }
            
            if (response.status.isSuccess()) {
                val responseJson = response.bodyAsText()
                val result = gson.fromJson(responseJson, UpdateProductResponse::class.java)
                Result.success(result)
            } else {
                Result.failure(Exception("Failed to update product: ${response.status}"))
            }
        } catch (e: Exception) {
            Log.e("RestApiClient", "Error updating product", e)
            Result.failure(e)
        }
    }
    
    suspend fun deleteProduct(id: Int): Result<DeleteProductResponse> {
        return try {
            val response: HttpResponse = client.delete("$baseUrl/products/$id")
            
            if (response.status.isSuccess()) {
                val json = response.bodyAsText()
                val result = gson.fromJson(json, DeleteProductResponse::class.java)
                Result.success(result)
            } else {
                Result.failure(Exception("Failed to delete product: ${response.status}"))
            }
        } catch (e: Exception) {
            Log.e("RestApiClient", "Error deleting product", e)
            Result.failure(e)
        }
    }
}
