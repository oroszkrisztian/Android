package com.tasty.recipesapp.api.client

import android.util.Log
import com.tasty.recipesapp.api.dto.ApiRecipeDTO
import com.tasty.recipesapp.api.service.RecipeService
import com.tasty.recipesapp.auth.manager.TokenManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipeApiClient {
    companion object {
        private const val BASE_URL = "https://recipe-appservice-cthjbdfafnhfdtes.germanywestcentral-01.azurewebsites.net/"
        private const val TAG = "RecipeApiClient"
    }

    private val recipeService: RecipeService by lazy {
        createRecipeService()
    }

    private fun createRecipeService(): RecipeService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecipeService::class.java)
    }

    suspend fun getRecipes(): Result<List<ApiRecipeDTO>> {
        return try {
            val response = recipeService.getRecipes()
            Log.d(TAG, "Successfully fetched ${response.size} recipes")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching recipes", e)
            Result.failure(e)
        }
    }

    suspend fun getRecipeById(id: Int): Result<ApiRecipeDTO> {
        return try {
            val response = recipeService.getRecipeById(id)
            Log.d(TAG, "Successfully fetched recipe $id")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching recipe $id", e)
            Result.failure(e)
        }
    }

    suspend fun addRecipe(recipe: ApiRecipeDTO): Result<ApiRecipeDTO> {
        return try {
            // Get token from TokenManager
            val token = TokenManager.getToken() ?: throw IllegalStateException("No auth token available")
            val response = recipeService.addRecipe(token, recipe)
            Log.d(TAG, "Successfully added recipe ${response.recipeID}")
            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding recipe", e)
            Result.failure(e)
        }
    }

    suspend fun getMyRecipes(): Result<List<ApiRecipeDTO>> {
        return try {
            val token = TokenManager.getToken() ?: throw IllegalStateException("No auth token available")
            val response = recipeService.getMyRecipes(token)

            // Filter recipes to ensure they belong to the authorized user
            val filteredRecipes = response.filter { it.userEmail == "orosz.krisztian@student.ms.sapientia.ro" }

            // Check if filtered recipes are empty (unauthorized recipes found)
            if (filteredRecipes.isEmpty()) {
                throw SecurityException("Access denied: No recipes found for the authorized user")
            }

            Log.d(TAG, "Successfully fetched my recipes: ${filteredRecipes.size}")
            Result.success(filteredRecipes)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching my recipes", e)
            Result.failure(e)
        }

    }

    suspend fun deleteRecipe(token: String, recipeId: Int): Result<Unit> {
        return try {
            recipeService.deleteRecipe(token, recipeId)
            Log.d(TAG, "Successfully deleted recipe: $recipeId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting recipe $recipeId", e)
            Log.e(TAG, "Error message: ${e.message}")
            Result.failure(e)
        }
    }
}