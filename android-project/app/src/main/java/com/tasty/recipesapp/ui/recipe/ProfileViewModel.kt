package com.tasty.recipesapp.Respository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.google.gson.Gson
import com.tasty.recipesapp.DTO.*
import com.tasty.recipesapp.Models.*
import com.tasty.recipesapp.Entity.RecipeEntity
import com.tasty.recipesapp.api.client.RecipeApiClient
import com.tasty.recipesapp.api.dto.ApiRecipeDTO
import com.tasty.recipesapp.dao.RecipeDao
import com.tasty.recipesapp.database.RecipeDatabase
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = RecipeDatabase.getDatabase(application).recipeDao()
    private val repository = RecipeRepository(dao)

    private val _recipes = MutableLiveData<List<ApiRecipeDTO>>()
    val recipes: LiveData<List<ApiRecipeDTO>> = _recipes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _favorites = MutableLiveData<List<ApiRecipeDTO>>()
    val favorites: LiveData<List<ApiRecipeDTO>> = _favorites

    init {
        loadRecipes()  // Add this
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favs = repository.getFavoriteRecipes()
                _favorites.value = favs
                Log.d("com.tasty.recipeapp", "Loaded ${favs.size} favorites")
            } catch (e: Exception) {
                _error.value = "Failed to load favorites: ${e.message}"
            }
        }
    }

    fun loadRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getRecipes().fold(
                onSuccess = { recipes ->
                    _recipes.value = recipes
                    _error.value = null
                    Log.d("com.tasty.recipeapp", "Loaded ${recipes.size} recipes")
                },
                onFailure = { e ->
                    _error.value = e.message
                }
            )
            _isLoading.value = false
        }
    }

    fun toggleFavorite(recipe: ApiRecipeDTO) {
        viewModelScope.launch {
            try {
                val currentFavorites = repository.getFavoriteRecipes()
                val isFavorited = currentFavorites.any { it.recipeID == recipe.recipeID }

                if (isFavorited) {
                    repository.deleteRecipe(recipe.recipeID)
                } else {
                    repository.saveRecipe(recipe)
                }

                loadFavorites() // Refresh favorites
                loadRecipes()   // Refresh recipes to update UI state
                Log.d("com.tasty.recipeapp", "Toggle favorite completed for recipe ${recipe.recipeID}")
            } catch (e: Exception) {
                Log.e("com.tasty.recipeapp", "Failed to toggle favorite for recipe ${recipe.recipeID}", e)
                _error.value = "Failed to update favorite: ${e.message}"
            }
        }
    }
}