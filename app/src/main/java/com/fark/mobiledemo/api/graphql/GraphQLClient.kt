package com.fark.mobiledemo.api.graphql

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.network.http.DefaultHttpEngine
import com.fark.mobiledemo.graphql.*
import com.fark.mobiledemo.models.*
import com.fark.mobiledemo.graphql.type.PaymentMethodInput
import com.fark.mobiledemo.graphql.type.AddressInput
import com.fark.mobiledemo.graphql.type.ProductSpecInput
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class GraphQLClient {
    private val graphqlUrl = "http://10.0.2.2:3000/graphql"
    
    // Logging interceptor to debug actual URLs
    private val loggingInterceptor = Interceptor { chain ->
        val request = chain.request()
        Log.d(TAG, "=== Apollo HTTP Request ===")
        Log.d(TAG, "URL: ${request.url}")
        Log.d(TAG, "Method: ${request.method}")
        Log.d(TAG, "Headers: ${request.headers}")
        
        try {
            val response = chain.proceed(request)
            Log.d(TAG, "Response Code: ${response.code}")
            response
        } catch (e: Exception) {
            Log.e(TAG, "Request failed: ${e.message}", e)
            throw e
        }
    }
    
    // Create fresh OkHttp client with no connection pooling
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectionPool(ConnectionPool(0, 1, TimeUnit.MILLISECONDS))
        .retryOnConnectionFailure(false)
        .build()
    
    private val apolloClient = ApolloClient.Builder()
        .serverUrl(graphqlUrl)
        .httpEngine(DefaultHttpEngine(okHttpClient))
        .build()
    
    init {
        Log.d(TAG, "Using GraphQL URL: $graphqlUrl")
    }
    
    companion object {
        private const val TAG = "GraphQLClient"
    }
    
    // Users CRUD
    suspend fun getUsers(): Result<List<User>> {
        return try {
            Log.d(TAG, "Fetching users from GraphQL...")
            val response = apolloClient.query(GetUsersQuery()).execute()
            
            // Log raw response for debugging
            Log.d(TAG, "Response hasErrors: ${response.hasErrors()}")
            Log.d(TAG, "Response errors: ${response.errors}")
            Log.d(TAG, "Response data: ${response.data}")
            
            if (response.hasErrors()) {
                val errorMessage = response.errors?.joinToString(", ") { it.message }
                Log.e(TAG, "GraphQL errors: $errorMessage")
                return Result.failure(Exception("GraphQL Error: $errorMessage"))
            }
            
            if (response.data?.users != null) {
                val users = response.data!!.users.map { userData ->
                    User(
                        id = userData.id.toInt(),
                        email = userData.email,
                        name = userData.name,
                        status = UserStatus.valueOf(userData.status.name),
                        description = userData.description,
                        metadata = userData.metadata as? Map<String, Any>,
                        tags = userData.tags
                    )
                }
                Log.d(TAG, "Successfully fetched ${users.size} users")
                Result.success(users)
            } else {
                Log.e(TAG, "Response data is null")
                Result.failure(Exception("No data returned from server"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception fetching users", e)
            Result.failure(e)
        }
    }
    
    suspend fun createUser(user: User): Result<User> {
        return try {
            // Use default CREDIT_CARD payment method
            val paymentMethodInput = PaymentMethodInput(
                type = Optional.present("Visa"),
                last4 = Optional.present("1234"),
                bank = Optional.absent(),
                email = Optional.absent()
            )
            
            val mutation = CreateUserMutation(
                email = user.email,
                name = user.name,
                status = when (user.status) {
                    UserStatus.ACTIVE -> com.fark.mobiledemo.graphql.type.UserStatus.ACTIVE
                    UserStatus.INACTIVE -> com.fark.mobiledemo.graphql.type.UserStatus.INACTIVE
                    UserStatus.PENDING -> com.fark.mobiledemo.graphql.type.UserStatus.PENDING
                },
                description = Optional.presentIfNotNull(user.description),
                metadata = Optional.presentIfNotNull(user.metadata),
                tags = user.tags,
                paymentMethod = paymentMethodInput
            )
            val response = apolloClient.mutation(mutation).execute()
            
            if (response.data?.createUser != null) {
                val createdUser = response.data!!.createUser
                Result.success(
                    User(
                        id = createdUser.id.toInt(),
                        email = createdUser.email,
                        name = createdUser.name,
                        status = UserStatus.valueOf(createdUser.status.name),
                        description = null,
                        metadata = null,
                        tags = emptyList()
                    )
                )
            } else {
                val errors = response.errors?.joinToString { it.message } ?: "Unknown error"
                Result.failure(Exception("Failed to create user: $errors"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUser(user: User): Result<User> {
        return try {
            // Use default CREDIT_CARD payment method
            val paymentMethodInput = PaymentMethodInput(
                type = Optional.present("Visa"),
                last4 = Optional.present("1234"),
                bank = Optional.absent(),
                email = Optional.absent()
            )
            
            val mutation = UpdateUserMutation(
                id = user.id.toString(),
                email = user.email,
                name = user.name,
                status = when (user.status) {
                    UserStatus.ACTIVE -> com.fark.mobiledemo.graphql.type.UserStatus.ACTIVE
                    UserStatus.INACTIVE -> com.fark.mobiledemo.graphql.type.UserStatus.INACTIVE
                    UserStatus.PENDING -> com.fark.mobiledemo.graphql.type.UserStatus.PENDING
                },
                description = Optional.presentIfNotNull(user.description),
                metadata = Optional.presentIfNotNull(user.metadata),
                tags = user.tags,
                paymentMethod = paymentMethodInput
            )
            val response = apolloClient.mutation(mutation).execute()
            
            if (response.data?.updateUser != null) {
                val updatedUser = response.data!!.updateUser
                Result.success(
                    User(
                        id = updatedUser.id.toInt(),
                        email = updatedUser.email,
                        name = updatedUser.name,
                        status = UserStatus.valueOf(updatedUser.status.name),
                        description = null,
                        metadata = null,
                        tags = emptyList()
                    )
                )
            } else {
                val errors = response.errors?.joinToString { it.message } ?: "Unknown error"
                Result.failure(Exception("Failed to update user: $errors"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteUser(id: Int): Result<Boolean> {
        return try {
            val mutation = DeleteUserMutation(id.toString())
            val response = apolloClient.mutation(mutation).execute()
            if (response.data?.deleteUser == true) {
                Result.success(true)
            } else {
                val errors = response.errors?.joinToString { it.message } ?: "Unknown error"
                Result.failure(Exception("Failed to delete user: $errors"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Products CRUD
    suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = apolloClient.query(GetProductsQuery()).execute()
            if (response.data?.products != null) {
                val products = response.data!!.products.map { productData ->
                    Product(
                        id = productData.id.toInt(),
                        name = productData.name,
                        price = productData.price,
                        category = productData.category,
                        inStock = productData.inStock,
                        specifications = productData.specifications.map {
                            ProductSpec(
                                key = it.key,
                                value = it.value
                            )
                        }
                    )
                }
                Result.success(products)
            } else {
                Result.failure(Exception("Failed to fetch products"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createProduct(product: Product): Result<Product> {
        return try {
            val mutation = CreateProductMutation(
                name = product.name,
                price = product.price,
                category = product.category,
                inStock = product.inStock,
                specifications = product.specifications.map {
                    ProductSpecInput(
                        key = it.key,
                        value = it.value
                    )
                }
            )
            val response = apolloClient.mutation(mutation).execute()
            
            if (response.data?.createProduct != null) {
                val createdProduct = response.data!!.createProduct
                Result.success(
                    Product(
                        id = createdProduct.id.toInt(),
                        name = createdProduct.name,
                        price = createdProduct.price,
                        category = createdProduct.category,
                        inStock = createdProduct.inStock,
                        specifications = emptyList()
                    )
                )
            } else {
                val errors = response.errors?.joinToString { it.message } ?: "Unknown error"
                Result.failure(Exception("Failed to create product: $errors"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateProduct(product: Product): Result<Product> {
        return try {
            val mutation = UpdateProductMutation(
                id = product.id.toString(),
                name = product.name,
                price = product.price,
                category = product.category,
                inStock = product.inStock,
                specifications = product.specifications.map {
                    ProductSpecInput(
                        key = it.key,
                        value = it.value
                    )
                }
            )
            val response = apolloClient.mutation(mutation).execute()
            
            if (response.data?.updateProduct != null) {
                val updatedProduct = response.data!!.updateProduct
                Result.success(
                    Product(
                        id = updatedProduct.id.toInt(),
                        name = updatedProduct.name,
                        price = updatedProduct.price,
                        category = updatedProduct.category,
                        inStock = updatedProduct.inStock,
                        specifications = emptyList()
                    )
                )
            } else {
                val errors = response.errors?.joinToString { it.message } ?: "Unknown error"
                Result.failure(Exception("Failed to update product: $errors"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteProduct(id: Int): Result<Boolean> {
        return try {
            val mutation = DeleteProductMutation(id.toString())
            val response = apolloClient.mutation(mutation).execute()
            if (response.data?.deleteProduct == true) {
                Result.success(true)
            } else {
                val errors = response.errors?.joinToString { it.message } ?: "Unknown error"
                Result.failure(Exception("Failed to delete product: $errors"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Orders CRUD
    suspend fun getOrders(): Result<List<Order>> {
        return try {
            val response = apolloClient.query(GetOrdersQuery()).execute()
            if (response.data?.orders != null) {
                val orders = response.data!!.orders.map { orderData ->
                    Order(
                        id = orderData.id.toInt(),
                        userId = orderData.userId.toInt(),
                        productIds = orderData.productIds.map { it.toInt() },
                        status = OrderStatus.valueOf(orderData.status.name),
                        total = orderData.total,
                        discountCode = orderData.discountCode,
                        shippingAddress = Address(
                            street = orderData.shippingAddress.street,
                            city = orderData.shippingAddress.city,
                            zipCode = orderData.shippingAddress.zipCode,
                            country = orderData.shippingAddress.country
                        )
                    )
                }
                Result.success(orders)
            } else {
                Result.failure(Exception("Failed to fetch orders"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createOrder(order: Order): Result<Order> {
        return try {
            val mutation = CreateOrderMutation(
                userId = order.userId.toString(),
                productIds = order.productIds.map { it.toString() },
                status = when (order.status) {
                    OrderStatus.CREATED -> com.fark.mobiledemo.graphql.type.OrderStatus.CREATED
                    OrderStatus.PROCESSING -> com.fark.mobiledemo.graphql.type.OrderStatus.PROCESSING
                    OrderStatus.SHIPPED -> com.fark.mobiledemo.graphql.type.OrderStatus.SHIPPED
                    OrderStatus.DELIVERED -> com.fark.mobiledemo.graphql.type.OrderStatus.DELIVERED
                },
                total = order.total,
                discountCode = Optional.presentIfNotNull(order.discountCode),
                shippingAddress = AddressInput(
                    street = order.shippingAddress.street,
                    city = order.shippingAddress.city,
                    zipCode = order.shippingAddress.zipCode,
                    country = order.shippingAddress.country
                )
            )
            val response = apolloClient.mutation(mutation).execute()
            
            if (response.data?.createOrder != null) {
                val createdOrder = response.data!!.createOrder
                Result.success(
                    Order(
                        id = createdOrder.id.toInt(),
                        userId = createdOrder.userId.toInt(),
                        productIds = createdOrder.productIds.map { it.toInt() },
                        status = OrderStatus.valueOf(createdOrder.status.name),
                        total = createdOrder.total,
                        discountCode = null,
                        shippingAddress = Address("", "", "", "")
                    )
                )
            } else {
                val errors = response.errors?.joinToString { it.message } ?: "Unknown error"
                Result.failure(Exception("Failed to create order: $errors"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateOrder(order: Order): Result<Order> {
        return try {
            val mutation = UpdateOrderMutation(
                id = order.id.toString(),
                userId = order.userId.toString(),
                productIds = order.productIds.map { it.toString() },
                status = when (order.status) {
                    OrderStatus.CREATED -> com.fark.mobiledemo.graphql.type.OrderStatus.CREATED
                    OrderStatus.PROCESSING -> com.fark.mobiledemo.graphql.type.OrderStatus.PROCESSING
                    OrderStatus.SHIPPED -> com.fark.mobiledemo.graphql.type.OrderStatus.SHIPPED
                    OrderStatus.DELIVERED -> com.fark.mobiledemo.graphql.type.OrderStatus.DELIVERED
                },
                total = order.total,
                discountCode = Optional.presentIfNotNull(order.discountCode),
                shippingAddress = AddressInput(
                    street = order.shippingAddress.street,
                    city = order.shippingAddress.city,
                    zipCode = order.shippingAddress.zipCode,
                    country = order.shippingAddress.country
                )
            )
            val response = apolloClient.mutation(mutation).execute()
            
            if (response.data?.updateOrder != null) {
                val updatedOrder = response.data!!.updateOrder
                Result.success(
                    Order(
                        id = updatedOrder.id.toInt(),
                        userId = updatedOrder.userId.toInt(),
                        productIds = updatedOrder.productIds.map { it.toInt() },
                        status = OrderStatus.valueOf(updatedOrder.status.name),
                        total = updatedOrder.total,
                        discountCode = null,
                        shippingAddress = Address("", "", "", "")
                    )
                )
            } else {
                val errors = response.errors?.joinToString { it.message } ?: "Unknown error"
                Result.failure(Exception("Failed to update order: $errors"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteOrder(id: Int): Result<Boolean> {
        return try {
            val mutation = DeleteOrderMutation(id.toString())
            val response = apolloClient.mutation(mutation).execute()
            if (response.data?.deleteOrder == true) {
                Result.success(true)
            } else {
                val errors = response.errors?.joinToString { it.message } ?: "Unknown error"
                Result.failure(Exception("Failed to delete order: $errors"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
