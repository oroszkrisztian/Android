package com.tasty.recipesapp.DTO

data class RecipeDTO(
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
    val components: List<ComponentDTO>,
    val instructions: List<InstructionDTO>,
    val nutrition: NutritionDTO
)

data class ComponentDTO(
    val rawText: String,
    val extraComment: String,
    val ingredient: IngredientDTO,
    val measurement: MeasurementDTO,
    val position: Int
)

data class IngredientDTO(
    val name: String
)

data class MeasurementDTO(
    val quantity: String,
    val unit: UnitDTO
)

data class UnitDTO(
    val name: String,
    val displaySingular: String,
    val displayPlural: String,
    val abbreviation: String
)

data class InstructionDTO(
    val instructionID: Int,
    val displayText: String,
    val position: Int
)

data class NutritionDTO(
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carbohydrates: Int,
    val sugar: Int,
    val fiber: Int
)
