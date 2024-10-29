package com.tasty.recipesapp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.tasty.recipesapp.R
import com.tasty.recipesapp.Respository.RecipeRepository
import com.tasty.recipesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Test JSON parsing with simple repository call
        val repository = RecipeRepository(applicationContext)
        val recipes = repository.getAllRecipes()

        // Log the results
        Log.d("Recipe", "Loaded ${recipes.size} recipes")
        recipes.forEach { recipe ->
            Log.d("Recipe", """
                Recipe: ${recipe.name}
                Description: ${recipe.description}
                Servings: ${recipe.servings}
                
                Components (${recipe.components.size}):
                ${recipe.components.joinToString("\n") { "- ${it.rawText}" }}
                
                Instructions (${recipe.instructions.size}):
                ${recipe.instructions.joinToString("\n") { "- ${it.text}" }}
                
                Nutrition:
                - Calories: ${recipe.nutrition.calories}
                - Protein: ${recipe.nutrition.protein}g
                - Fat: ${recipe.nutrition.fat}g
                - Carbs: ${recipe.nutrition.carbohydrates}g
            """.trimIndent())
        }

        // Change status bar color to green
        window.statusBarColor = getColor(R.color.green)

        // Set up Navigation Controller
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment // Use the correct ID
        navController = navHostFragment.navController


        binding.bottomNavigation.setupWithNavController(navController)

    }
}
