package com.tasty.recipesapp.ui.recipe

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tasty.recipesapp.Models.RecipeModel

import com.tasty.recipesapp.Respository.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipeListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RecipeRepository(application)

    // Private MutableLiveData that can be modified
    private val _recipes = MutableLiveData<List<RecipeModel>>()

    // Public LiveData that can only be observed
    val recipes: LiveData<List<RecipeModel>> = _recipes

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Use coroutine for background operation
                val recipeList = withContext(Dispatchers.IO) {
                    repository.getAllRecipes()
                }

                _recipes.value = recipeList

            } catch (e: Exception) {
                _error.value = "Failed to load recipes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Function to refresh data
    fun refreshRecipes() {
        loadRecipes()
    }
}