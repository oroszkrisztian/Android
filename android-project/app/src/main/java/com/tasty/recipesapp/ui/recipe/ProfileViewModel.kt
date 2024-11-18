package com.tasty.recipesapp.ui.recipe

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tasty.recipesapp.Models.RecipeModel
import com.tasty.recipesapp.Respository.RecipeRepository
import com.tasty.recipesapp.database.RecipeDatabase
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RecipeRepository(
        application,
        RecipeDatabase.getDatabase(application).recipeDao()
    )

    private val _myRecipes = MutableLiveData<List<RecipeModel>>()
    val myRecipes: LiveData<List<RecipeModel>> = _myRecipes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadMyRecipes()
    }

    private fun loadMyRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _myRecipes.value = repository.getAllLocalRecipes()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addRecipe(recipe: RecipeModel) {
        viewModelScope.launch {
            try {
                repository.insertRecipe(recipe)
                loadMyRecipes() // Reload the list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteRecipe(recipe: RecipeModel) {
        viewModelScope.launch {
            try {
                repository.deleteRecipe(recipe)
                loadMyRecipes() // Reload the list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}