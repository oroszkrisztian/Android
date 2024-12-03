package com.tasty.recipesapp.api.model

import com.tasty.recipesapp.DTO.RecipeDTO
import com.tasty.recipesapp.api.dto.ApiRecipeDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface RecipeService {
    @GET("api/recipes")
    suspend fun getRecipes(): List<ApiRecipeDTO>  // Directly specify List<ApiRecipeDTO>

    @GET("api/recipes/{id}")
    suspend fun getRecipeById(@Path("id") recipeId: Int): ApiRecipeDTO
}