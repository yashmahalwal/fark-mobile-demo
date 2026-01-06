package com.fark.mobiledemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.fark.mobiledemo.databinding.ActivityMainBinding
import com.fark.mobiledemo.fragments.OrdersFragment
import com.fark.mobiledemo.fragments.ProductsFragment
import com.fark.mobiledemo.fragments.UsersFragment
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        // Setup tabs
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Users"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Products"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Orders"))
        
        // Show initial fragment
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, UsersFragment())
            }
        }
        
        // Handle tab selection
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val fragment = when (tab?.position) {
                    0 -> UsersFragment()
                    1 -> ProductsFragment()
                    2 -> OrdersFragment()
                    else -> null
                }
                fragment?.let {
                    supportFragmentManager.commit {
                        replace(R.id.fragmentContainer, it)
                    }
                }
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}
