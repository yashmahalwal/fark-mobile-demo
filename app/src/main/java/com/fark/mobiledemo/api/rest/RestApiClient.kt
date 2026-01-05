package com.fark.mobiledemo.api.rest

import com.fark.mobiledemo.models.User
import com.fark.mobiledemo.models.Order
import com.fark.mobiledemo.models.Product
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RestApiClient {
    private val baseUrl = "http://10.0.2.2:3000/api/" // Android emulator localhost
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val apiService: RestApiService = retrofit.create(RestApiService::class.java)
    
    suspend fun getUser(id: Int): Result<User> {
        return try {
            val response = apiService.getUser(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch user: ${response.code()}"))
            }
        } catch (e: Exception) {
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
                paymentMethod = user.paymentMethod.name
            )
            val response = apiService.createUser(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOrder(id: Int): Result<Order> {
        return try {
            val response = apiService.getOrder(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch order: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = apiService.getProducts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch products: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

