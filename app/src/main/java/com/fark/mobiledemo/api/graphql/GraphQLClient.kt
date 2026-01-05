package com.fark.mobiledemo.api.graphql

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.fark.mobiledemo.graphql.GetUserQuery
import com.fark.mobiledemo.graphql.GetUsersQuery
import com.fark.mobiledemo.graphql.GetOrderQuery
import com.fark.mobiledemo.graphql.GetProductsQuery
import com.fark.mobiledemo.graphql.CreateUserMutation
import com.fark.mobiledemo.models.User
import com.fark.mobiledemo.models.Order
import com.fark.mobiledemo.models.Product
import com.fark.mobiledemo.models.UserStatus

class GraphQLClient {
    private val apolloClient = ApolloClient.Builder()
        .serverUrl("http://10.0.2.2:3000/graphql") // Android emulator localhost
        .build()
    
    suspend fun getUser(id: String): Result<User> {
        return try {
            val response = apolloClient.query(GetUserQuery(id)).execute()
            if (response.data?.user != null) {
                val userData = response.data!!.user!!
                Result.success(
                    User(
                        id = userData.id.toInt(),
                        email = userData.email,
                        name = userData.name,
                        status = UserStatus.valueOf(userData.status.name),
                        description = userData.description,
                        metadata = userData.metadata as? Map<String, Any>,
                        tags = userData.tags,
                        paymentMethod = com.fark.mobiledemo.models.PaymentMethod.valueOf(
                            when (userData.paymentMethod.__typename) {
                                "CreditCard" -> "CREDIT_CARD"
                                "DebitCard" -> "DEBIT_CARD"
                                "PayPal" -> "PAYPAL"
                                else -> "CREDIT_CARD"
                            }
                        )
                    )
                )
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
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
                        paymentMethod = com.fark.mobiledemo.models.PaymentMethod.CREDIT_CARD
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
    
    suspend fun getOrder(id: String): Result<Order> {
        return try {
            val response = apolloClient.query(GetOrderQuery(id)).execute()
            if (response.data?.order != null) {
                val orderData = response.data!!.order!!
                Result.success(
                    Order(
                        id = orderData.id.toInt(),
                        userId = orderData.userId.toInt(),
                        productIds = orderData.productIds.map { it.toInt() },
                        status = com.fark.mobiledemo.models.OrderStatus.valueOf(orderData.status.name),
                        total = orderData.total,
                        discountCode = orderData.discountCode,
                        shippingAddress = com.fark.mobiledemo.models.Address(
                            street = orderData.shippingAddress.street,
                            city = orderData.shippingAddress.city,
                            zipCode = orderData.shippingAddress.zipCode,
                            country = orderData.shippingAddress.country
                        )
                    )
                )
            } else {
                Result.failure(Exception("Order not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
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
                            com.fark.mobiledemo.models.ProductSpec(
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
    
    suspend fun createUser(user: User): Result<User> {
        return try {
            val response = apolloClient.mutation(
                CreateUserMutation(
                    email = user.email,
                    name = user.name,
                    status = when (user.status) {
                        UserStatus.ACTIVE -> com.fark.mobiledemo.graphql.UserStatus.ACTIVE
                        UserStatus.INACTIVE -> com.fark.mobiledemo.graphql.UserStatus.INACTIVE
                        UserStatus.PENDING -> com.fark.mobiledemo.graphql.UserStatus.PENDING
                    },
                    description = Optional.presentIfNotNull(user.description),
                    metadata = Optional.presentIfNotNull(user.metadata),
                    tags = user.tags,
                    paymentMethod = when (user.paymentMethod) {
                        com.fark.mobiledemo.models.PaymentMethod.CREDIT_CARD -> 
                            com.fark.mobiledemo.graphql.PaymentMethod(
                                creditCard = com.fark.mobiledemo.graphql.CreditCard(
                                    type = "Visa",
                                    last4 = "1234"
                                )
                            )
                        com.fark.mobiledemo.models.PaymentMethod.DEBIT_CARD ->
                            com.fark.mobiledemo.graphql.PaymentMethod(
                                debitCard = com.fark.mobiledemo.graphql.DebitCard(
                                    type = "Debit",
                                    bank = "Test Bank"
                                )
                            )
                        com.fark.mobiledemo.models.PaymentMethod.PAYPAL ->
                            com.fark.mobiledemo.graphql.PaymentMethod(
                                payPal = com.fark.mobiledemo.graphql.PayPal(
                                    email = "test@paypal.com"
                                )
                            )
                    }
                )
            ).execute()
            
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
                        paymentMethod = com.fark.mobiledemo.models.PaymentMethod.CREDIT_CARD
                    )
                )
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

