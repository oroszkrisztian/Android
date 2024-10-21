package com.tasty.recipesapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tasty.recipesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Change status bar color to green
        window.statusBarColor = getColor(R.color.green)

        binding.getStartedButton.setOnClickListener {
            Log.i("MainActivity", "Get Started")
        }
    }
}