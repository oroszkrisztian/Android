package com.tasty.recipesapp.ui.recipe

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tasty.recipesapp.Models.RecipeModel
import com.tasty.recipesapp.Respository.RecipeRepository
import com.tasty.recipesapp.database.RecipeDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RecipeRepository(
        application,
        RecipeDatabase.getDatabase(application).recipeDao()
    )

    // For recipes from JSON
    private val _recipes = MutableLiveData<List<RecipeModel>>()
    val recipes: LiveData<List<RecipeModel>> = _recipes

    // For user's local recipes
    private val _localRecipes = MutableLiveData<List<RecipeModel>>()
    val localRecipes: LiveData<List<RecipeModel>> = _localRecipes

    private val _randomRecipe = MutableLiveData<RecipeModel>()
    val randomRecipe: LiveData<RecipeModel> = _randomRecipe

    private val _selectedRecipe = MutableLiveData<RecipeModel>()
    val selectedRecipe: LiveData<RecipeModel> = _selectedRecipe

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        //loadRecipes()
        loadLocalRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val recipeList = withContext(Dispatchers.IO) {
                    repository.getAllRecipes()
                }
                Log.d("RecipeListViewModel", "Loaded ${recipeList.size} recipes")
                _recipes.value = recipeList
            } catch (e: Exception) {
                Log.e("RecipeListViewModel", "Error loading recipes", e)
                _error.value = "Failed to load recipes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadLocalRecipes() {
        viewModelScope.launch {
            try {
                val recipes = withContext(Dispatchers.IO) {
                    repository.getAllLocalRecipes()
                }
                _localRecipes.value = recipes
            } catch (e: Exception) {
                _error.value = "Failed to load local recipes: ${e.message}"
            }
        }
    }

    fun getRecipeById(recipeId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Check local recipes first
                _localRecipes.value?.find { it.id == recipeId }?.let { recipe ->
                    _selectedRecipe.value = recipe
                    return@launch
                }

                // Then check JSON recipes
                _recipes.value?.find { it.id == recipeId }?.let { recipe ->
                    _selectedRecipe.value = recipe
                    return@launch
                }

                // If not found in current lists, try loading from repository
                val recipeList = withContext(Dispatchers.IO) {
                    repository.getAllRecipes()
                }

                recipeList.find { it.id == recipeId }?.let { recipe ->
                    _selectedRecipe.value = recipe
                } ?: run {
                    _error.value = "Recipe not found"
                }

            } catch (e: Exception) {
                _error.value = "Failed to load recipe: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


}