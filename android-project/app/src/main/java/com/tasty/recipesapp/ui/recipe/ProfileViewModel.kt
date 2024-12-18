package com.tasty.recipesapp.Respository

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tasty.recipesapp.api.dto.ApiRecipeDTO
import com.tasty.recipesapp.database.RecipeDatabase
import kotlinx.coroutines.launch

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

    private val _selectedRecipe = MutableLiveData<ApiRecipeDTO>()
    val selectedRecipe: LiveData<ApiRecipeDTO> = _selectedRecipe

    private var showingMyRecipes = false

    init {
        loadRecipes()
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
                    repository.removeFromFav(recipe.recipeID)
                } else {
                    repository.saveRecipeFav(recipe)
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


    fun getRecipeById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val recipe = repository.getRecipeById(id)
                _selectedRecipe.value = recipe
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load recipe: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    fun addNewRecipe(recipe: ApiRecipeDTO) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.addRecipe(recipe).fold(
                    onSuccess = { addedRecipe ->
                        _error.value = null
                        loadRecipes() // Refresh recipe list
                        Log.d("com.tasty.recipeapp", "Recipe added successfully: ${addedRecipe.recipeID}")
                    },
                    onFailure = { e ->
                        _error.value = "Failed to add recipe: ${e.message}"
                        Log.e("com.tasty.recipeapp", "Failed to add recipe", e)
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMyRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getMyRecipes().fold(
                onSuccess = { recipes ->
                    _recipes.value = recipes
                    _error.value = null
                },
                onFailure = { e ->
                    _recipes.value = emptyList() // Set to empty list on failure
                    _error.value = e.message
                }
            )
            _isLoading.value = false
        }
    }






    fun deleteRecipe(recipeId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteRecipe(recipeId)
                // Refresh the list after deletion
                if (showingMyRecipes) {
                    loadMyRecipes()
                } else {
                    loadRecipes()
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete recipe: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}