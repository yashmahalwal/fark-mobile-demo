package com.fark.mobiledemo.api.graphql

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.fark.mobiledemo.graphql.*
import com.fark.mobiledemo.models.*
import com.fark.mobiledemo.graphql.type.PaymentMethodInput
import com.fark.mobiledemo.graphql.type.AddressInput
import com.fark.mobiledemo.graphql.type.ProductSpecInput

class GraphQLClient {
    private val apolloClient = ApolloClient.Builder()
        .serverUrl("http://10.0.2.2:3000/graphql") // Android emulator localhost
        .build()
    
    // Users CRUD
    suspend fun getUsers(): Result<List<User>> {
        return try {
            val response = apolloClient.query(GetUsersQuery()).execute()
            if (response.data?.users != null) {
                val users = response.data!!.users.map { userData ->
                    User(
                        id = userData.id.toInt(),
                        email = userData.email,
                        name = userData.name,
                        status = UserStatus.valueOf(userData.status.name),
                        description = userData.description,
                        metadata = userData.metadata as? Map<String, Any>,
                        tags = userData.tags,
                        paymentMethod = when (userData.paymentMethod.__typename) {
                            "CreditCard" -> PaymentMethod.CREDIT_CARD
                            "DebitCard" -> PaymentMethod.DEBIT_CARD
                            "PayPal" -> PaymentMethod.PAYPAL
                            else -> PaymentMethod.CREDIT_CARD
                        }
                    )
                }
                Result.success(users)
            } else {
                Result.failure(Exception("Failed to fetch users"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createUser(user: User): Result<User> {
        return try {
            val paymentMethodInput = when (user.paymentMethod) {
                PaymentMethod.CREDIT_CARD -> 
                    PaymentMethodInput(
                        type = Optional.present("Visa"),
                        last4 = Optional.present("1234"),
                        bank = Optional.absent(),
                        email = Optional.absent()
                    )
                PaymentMethod.DEBIT_CARD ->
                    PaymentMethodInput(
                        type = Optional.present("Debit"),
                        bank = Optional.present("Test Bank"),
                        last4 = Optional.absent(),
                        email = Optional.absent()
                    )
                PaymentMethod.PAYPAL ->
                    PaymentMethodInput(
                        email = Optional.present("test@paypal.com"),
                        type = Optional.absent(),
                        last4 = Optional.absent(),
                        bank = Optional.absent()
                    )
            }
            
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
                        tags = emptyList(),
                        paymentMethod = PaymentMethod.CREDIT_CARD
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
            val paymentMethodInput = when (user.paymentMethod) {
                PaymentMethod.CREDIT_CARD -> 
                    PaymentMethodInput(
                        type = Optional.present("Visa"),
                        last4 = Optional.present("1234"),
                        bank = Optional.absent(),
                        email = Optional.absent()
                    )
                PaymentMethod.DEBIT_CARD ->
                    PaymentMethodInput(
                        type = Optional.present("Debit"),
                        bank = Optional.present("Test Bank"),
                        last4 = Optional.absent(),
                        email = Optional.absent()
                    )
                PaymentMethod.PAYPAL ->
                    PaymentMethodInput(
                        email = Optional.present("test@paypal.com"),
                        type = Optional.absent(),
                        last4 = Optional.absent(),
                        bank = Optional.absent()
                    )
            }
            
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
                        tags = emptyList(),
                        paymentMethod = PaymentMethod.CREDIT_CARD
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
