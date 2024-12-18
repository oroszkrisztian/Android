package com.tasty.recipesapp.Respository

import android.util.Log
import com.google.gson.Gson
import com.tasty.recipesapp.Entity.RecipeEntity
import com.tasty.recipesapp.api.client.RecipeApiClient
import com.tasty.recipesapp.api.dto.ApiRecipeDTO
import com.tasty.recipesapp.auth.manager.TokenManager
import com.tasty.recipesapp.dao.RecipeDao

class RecipeRepository(private val recipeDao: RecipeDao) {
    private val apiClient = RecipeApiClient()

    suspend fun getRecipes(): Result<List<ApiRecipeDTO>> = apiClient.getRecipes()


    //add to fav
    suspend fun saveRecipeFav(recipe: ApiRecipeDTO) {
        Log.d("com.tasty.recipeapp", "Saving recipe: ${recipe.recipeID}")
        val recipeEntity = RecipeEntity(
            recipeId = recipe.recipeID,
            json = Gson().toJson(recipe)
        )
        val id = recipeDao.insertRecipe(recipeEntity)
        Log.d("com.tasty.recipeapp", "Recipe saved with ID: $id")
        val count = recipeDao.getRecipeCount()
        Log.d("com.tasty.recipeapp", "Total recipes after save: $count")
    }

    //load fav
    suspend fun getFavoriteRecipes(): List<ApiRecipeDTO> {
        val favorites = recipeDao.getAllRecipes()
        Log.d("com.tasty.recipeapp", "Raw favorites from DB: ${favorites.size}")
        return favorites.map { entity ->
            try {
                Gson().fromJson(entity.json, ApiRecipeDTO::class.java)
            } catch (e: Exception) {
                Log.e("com.tasty.recipeapp", "Failed to parse JSON: ${entity.json}", e)
                throw e
            }
        }
    }


    //delete from fav
    suspend fun removeFromFav(recipeId: Int) {
        recipeDao.removeFromFav(recipeId)
    }

    suspend fun addRecipe(recipe: ApiRecipeDTO): Result<ApiRecipeDTO> {
        return try {
            apiClient.addRecipe(recipe)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to add recipe", e)
            Result.failure(e)
        }
    }

    suspend fun getRecipeById(id: Int): ApiRecipeDTO {
        return apiClient.getRecipeById(id).getOrThrow()
    }

    suspend fun getMyRecipes(): Result<List<ApiRecipeDTO>> = apiClient.getMyRecipes()

    suspend fun deleteRecipe(recipeId: Int): Result<Unit> {
        return try {
            val token = TokenManager.getToken() ?: throw IllegalStateException("No auth token available")
            apiClient.deleteRecipe(token, recipeId)
        } catch (e: Exception) {
            Log.e("Repository", "Failed to delete recipe", e)
            Result.failure(e)
        }
    }

}