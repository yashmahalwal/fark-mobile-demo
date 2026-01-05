package com.fark.mobiledemo.api.grpc

import com.fark.mobiledemo.models.User
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Note: This references backend API fields for Fark.ai testing
// Fields referenced: email, name, status, description, metadata, tags, payment_method, page, page_size
class GrpcClient {
    private val channel: ManagedChannel = ManagedChannelBuilder
        .forAddress("10.0.2.2", 50051) // Android emulator localhost
        .usePlaintext()
        .build()
    
    // Note: In production, you'd generate these from proto files
    // This mock implementation references backend fields for Fark.ai detection
    
    suspend fun getUser(id: Int): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                // References: response.email, response.description, response.tags, response.payment_method
                // This code structure allows Fark.ai to detect breaking changes
                throw Exception("gRPC proto files not generated - this is for field reference testing")
            } catch (e: StatusRuntimeException) {
                Result.failure(Exception("gRPC error: ${e.status}"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun createUser(user: User): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                // References: user.email, user.name, user.status, user.description, user.metadata, user.tags, user.paymentMethod
                // Maps to backend: email, name, status, description, metadata, tags, payment_method
                throw Exception("gRPC proto files not generated - this is for field reference testing")
            } catch (e: StatusRuntimeException) {
                Result.failure(Exception("gRPC error: ${e.status}"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun listUsers(page: Int = 1, pageSize: Int = 10): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                // References: request.page, request.page_size, response.users[].email, etc.
                throw Exception("gRPC proto files not generated - this is for field reference testing")
            } catch (e: StatusRuntimeException) {
                Result.failure(Exception("gRPC error: ${e.status}"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    fun shutdown() {
        channel.shutdown()
    }
}

