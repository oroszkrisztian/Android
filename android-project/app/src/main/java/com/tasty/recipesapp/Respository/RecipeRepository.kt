package com.tasty.recipesapp.Respository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.tasty.recipesapp.DTO.*
import com.tasty.recipesapp.Models.*
import com.tasty.recipesapp.Entity.RecipeEntity
import com.tasty.recipesapp.api.client.RecipeApiClient
import com.tasty.recipesapp.api.dto.ApiRecipeDTO
import com.tasty.recipesapp.api.service.RecipeService
import com.tasty.recipesapp.dao.RecipeDao
import org.json.JSONObject
import java.io.IOException

class RecipeRepository(private val recipeDao: RecipeDao) {
    private val apiClient = RecipeApiClient()

    suspend fun getRecipes(): Result<List<ApiRecipeDTO>> = apiClient.getRecipes()

    suspend fun saveRecipe(recipe: ApiRecipeDTO) {
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



    suspend fun deleteRecipe(recipeId: Int) {
        recipeDao.deleteByApiId(recipeId)
    }

    suspend fun addRecipe(recipe: ApiRecipeDTO): Result<ApiRecipeDTO> {
        return apiClient.addRecipe(recipe)
    }

    suspend fun getRecipeById(id: Int): ApiRecipeDTO {
        return apiClient.getRecipeById(id).getOrThrow()
    }
}