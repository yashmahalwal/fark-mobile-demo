package com.fark.mobiledemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fark.mobiledemo.api.rest.RestApiClient
import com.fark.mobiledemo.databinding.ActivityMainBinding
import com.fark.mobiledemo.models.User
import com.fark.mobiledemo.models.UserStatus
import com.fark.mobiledemo.models.PaymentMethod
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val restApi = RestApiClient()
    
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
}
