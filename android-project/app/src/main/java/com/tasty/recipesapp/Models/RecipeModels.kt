package com.tasty.recipesapp.Models

data class RecipeModel(
    val id: Int,
    val name: String,
    val description: String,
    val thumbnailUrl: String,
    val servings: Int,
    val components: List<ComponentModel>,
    val instructions: List<InstructionModel>,
    val nutrition: NutritionModel,
    val isFavorite: Boolean = false  // Add this field with default false
)

data class ComponentModel(
    val rawText: String,
    val ingredient: String,
    val amount: String,
    val position: Int
)

data class InstructionModel(
    val id: Int,
    val text: String,
    val position: Int
)

data class NutritionModel(
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carbohydrates: Int
)
