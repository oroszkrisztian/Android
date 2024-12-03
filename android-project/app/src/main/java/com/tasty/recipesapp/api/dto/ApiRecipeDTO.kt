package com.tasty.recipesapp.api.dto

data class ApiRecipeDTO(
    val recipeID: Int,
    val name: String,
    val description: String,
    val thumbnailUrl: String,
    val keywords: String,
    val isPublic: Boolean,
    val userEmail: String,
    val originalVideoUrl: String,
    val country: String,
    val numServings: Int,
    val components: List<ApiComponentDTO> = emptyList(),
    val instructions: List<ApiInstructionDTO> = emptyList(),
    val isFavorite: Boolean = false
)

data class ApiComponentDTO(
    val rawText: String,
    val ingredient: ApiIngredientDTO,
    val measurement: ApiMeasurementDTO,
    val position: Int
)

data class ApiIngredientDTO(
    val name: String
)

data class ApiMeasurementDTO(
    val quantity: String,
    val unit: ApiUnitDTO
)

data class ApiUnitDTO(
    val name: String,
    val displaySingular: String,
    val displayPlural: String,
    val abbreviation: String
)

data class ApiInstructionDTO(
    val instructionID: Int,
    val displayText: String,
    val position: Int
)