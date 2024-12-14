package com.tasty.recipesapp.api.service



import com.tasty.recipesapp.api.dto.ApiRecipeDTO
import com.tasty.recipesapp.model.RecipeResponse
import com.tasty.recipesapp.model.SingleRecipeResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RecipeService {
    @GET("api/recipes")
    suspend fun getRecipes(): List<ApiRecipeDTO>

    @GET("api/recipes/{id}")
    suspend fun getRecipeById(@Path("id") recipeId: Int): ApiRecipeDTO

    @POST("api/recipes")
    suspend fun addRecipe(
        @Header("Authorization") authorization: String,
        @Body recipe: ApiRecipeDTO
    ): ApiRecipeDTO

    @GET("api/recipes/my")
    suspend fun getMyRecipes(
        @Header("Authorization") authorization: String
    ): List<ApiRecipeDTO>

}