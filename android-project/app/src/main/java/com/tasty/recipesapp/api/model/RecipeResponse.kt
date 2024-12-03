package com.tasty.recipesapp.model

import com.tasty.recipesapp.api.dto.ApiRecipeDTO

data class RecipeResponse(
    val status: Int,
    val message: String,
    val data: List<ApiRecipeDTO>
)

data class SingleRecipeResponse(
    val status: Int,
    val message: String,
    val data: ApiRecipeDTO
)