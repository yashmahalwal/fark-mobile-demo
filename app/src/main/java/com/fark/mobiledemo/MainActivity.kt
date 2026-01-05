package com.fark.mobiledemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fark.mobiledemo.api.grpc.GrpcClient
import com.fark.mobiledemo.api.graphql.GraphQLClient
import com.fark.mobiledemo.api.rest.RestApiClient
import com.fark.mobiledemo.databinding.ActivityMainBinding
import com.fark.mobiledemo.models.User
import com.fark.mobiledemo.models.UserStatus
import com.fark.mobiledemo.models.PaymentMethod
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val restApi = RestApiClient()
    private val graphqlClient = GraphQLClient()
    private val grpcClient = GrpcClient()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // REST API buttons
        binding.btnRestGetUser.setOnClickListener {
            fetchUserViaRest()
        }
        
        binding.btnRestCreateUser.setOnClickListener {
            createUserViaRest()
        }
        
        // GraphQL API buttons
        binding.btnGraphqlGetUser.setOnClickListener {
            fetchUserViaGraphQL()
        }
        
        binding.btnGraphqlCreateUser.setOnClickListener {
            createUserViaGraphQL()
        }
        
        // gRPC API buttons
        binding.btnGrpcGetUser.setOnClickListener {
            fetchUserViaGrpc()
        }
        
        binding.btnGrpcListUsers.setOnClickListener {
            listUsersViaGrpc()
        }
    }
    
    private fun fetchUserViaRest() {
        lifecycleScope.launch {
            binding.tvRestResult.text = "Loading..."
            val userId = binding.etRestUserId.text.toString().toIntOrNull() ?: 1
            
            restApi.getUser(userId).fold(
                onSuccess = { user ->
                    binding.tvRestResult.text = """
                        REST User:
                        ID: ${user.id}
                        Email: ${user.email}
                        Name: ${user.name}
                        Status: ${user.status}
                        Description: ${user.description ?: "N/A"}
                        Tags: ${user.tags.joinToString()}
                        Payment: ${user.paymentMethod}
                    """.trimIndent()
                },
                onFailure = { error ->
                    binding.tvRestResult.text = "Error: ${error.message}"
                }
            )
        }
    }
    
    private fun createUserViaRest() {
        lifecycleScope.launch {
            val newUser = User(
                id = 0,
                email = "test@example.com",
                name = "Test User",
                status = UserStatus.ACTIVE,
                description = "Test description",
                metadata = mapOf("key" to "value"),
                tags = listOf("tag1", "tag2"),
                paymentMethod = PaymentMethod.CREDIT_CARD
            )
            
            restApi.createUser(newUser).fold(
                onSuccess = { response ->
                    Toast.makeText(this@MainActivity, "User created: ${response.id}", Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
    
    private fun fetchUserViaGraphQL() {
        lifecycleScope.launch {
            binding.tvGraphqlResult.text = "Loading..."
            val userId = binding.etGraphqlUserId.text.toString()
            
            graphqlClient.getUser(userId).fold(
                onSuccess = { user ->
                    binding.tvGraphqlResult.text = """
                        GraphQL User:
                        ID: ${user.id}
                        Email: ${user.email}
                        Name: ${user.name}
                        Status: ${user.status}
                        Description: ${user.description ?: "N/A"}
                        Tags: ${user.tags.joinToString()}
                        Payment: ${user.paymentMethod}
                    """.trimIndent()
                },
                onFailure = { error ->
                    binding.tvGraphqlResult.text = "Error: ${error.message}"
                }
            )
        }
    }
    
    private fun createUserViaGraphQL() {
        lifecycleScope.launch {
            val newUser = User(
                id = 0,
                email = "graphql@example.com",
                name = "GraphQL User",
                status = UserStatus.ACTIVE,
                description = "GraphQL test",
                metadata = mapOf("source" to "graphql"),
                tags = listOf("graphql", "test"),
                paymentMethod = PaymentMethod.PAYPAL
            )
            
            graphqlClient.createUser(newUser).fold(
                onSuccess = { user ->
                    Toast.makeText(this@MainActivity, "User created: ${user.id}", Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
    
    private fun fetchUserViaGrpc() {
        lifecycleScope.launch {
            binding.tvGrpcResult.text = "Loading..."
            val userId = binding.etGrpcUserId.text.toString().toIntOrNull() ?: 1
            
            grpcClient.getUser(userId).fold(
                onSuccess = { user ->
                    binding.tvGrpcResult.text = """
                        gRPC User:
                        ID: ${user.id}
                        Email: ${user.email}
                        Name: ${user.name}
                        Status: ${user.status}
                        Description: ${user.description ?: "N/A"}
                        Tags: ${user.tags.joinToString()}
                        Payment: ${user.paymentMethod}
                    """.trimIndent()
                },
                onFailure = { error ->
                    binding.tvGrpcResult.text = "Error: ${error.message}"
                }
            )
        }
    }
    
    private fun listUsersViaGrpc() {
        lifecycleScope.launch {
            binding.tvGrpcResult.text = "Loading..."
            
            grpcClient.listUsers(page = 1, pageSize = 10).fold(
                onSuccess = { users ->
                    binding.tvGrpcResult.text = """
                        gRPC Users (${users.size}):
                        ${users.joinToString("\n") { "ID: ${it.id}, Email: ${it.email}, Name: ${it.name}" }}
                    """.trimIndent()
                },
                onFailure = { error ->
                    binding.tvGrpcResult.text = "Error: ${error.message}"
                }
            )
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        grpcClient.shutdown()
    }
}

