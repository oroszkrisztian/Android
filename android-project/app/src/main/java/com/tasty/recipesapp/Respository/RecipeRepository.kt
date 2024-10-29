package com.tasty.recipesapp.Respository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.tasty.recipesapp.DTO.ComponentDTO
import com.tasty.recipesapp.DTO.InstructionDTO
import com.tasty.recipesapp.DTO.NutritionDTO
import com.tasty.recipesapp.DTO.RecipeDTO
import com.tasty.recipesapp.Models.ComponentModel
import com.tasty.recipesapp.Models.InstructionModel
import com.tasty.recipesapp.Models.NutritionModel
import com.tasty.recipesapp.Models.RecipeModel

import java.io.IOException

class RecipeRepository(private val context: Context) {
    private val gson = Gson()

    fun getAllRecipes(): List<RecipeModel> {
        return readRecipesFromJson().map { it.toModel() }
    }

    private fun readRecipesFromJson(): List<RecipeDTO> {
        try {
            val jsonString = context.assets
                .open("recipe_details.json")
                .bufferedReader()
                .use { it.readText() }

            // Create a data class to hold the recipes array
            data class RecipeResponse(val recipes: List<RecipeDTO>)

            // Parse the JSON response
            val response = gson.fromJson(jsonString, RecipeResponse::class.java)
            Log.d("Repository", "Successfully loaded ${response.recipes.size} recipes")
            return response.recipes

        } catch (e: IOException) {
            Log.e("Repository", "Error reading recipes file: ${e.message}")
            return emptyList()
        } catch (e: Exception) {
            Log.e("Repository", "Error parsing recipes: ${e.message}")
            return emptyList()
        }
    }


    // Extension functions for mapping with unique names
    private fun RecipeDTO.toModel(): RecipeModel {
        return RecipeModel(
            id = this.recipeID,
            name = this.name,
            description = this.description,
            thumbnailUrl = this.thumbnailUrl,
            servings = this.numServings,
            components = this.components.mapToComponentModels(),
            instructions = this.instructions.mapToInstructionModels(),
            nutrition = this.nutrition.toModel()
        )
    }

    private fun List<ComponentDTO>.mapToComponentModels(): List<ComponentModel> {
        return this.map { it.toModel() }
    }

    private fun ComponentDTO.toModel(): ComponentModel {
        return ComponentModel(
            rawText = this.rawText,
            ingredient = this.ingredient.name,
            amount = "${this.measurement.quantity} ${this.measurement.unit.abbreviation}",
            position = this.position
        )
    }

    private fun List<InstructionDTO>.mapToInstructionModels(): List<InstructionModel> {
        return this.map { it.toModel() }
    }

    private fun InstructionDTO.toModel(): InstructionModel {
        return InstructionModel(
            id = this.instructionID,
            text = this.displayText,
            position = this.position
        )
    }

    private fun NutritionDTO.toModel(): NutritionModel {
        return NutritionModel(
            calories = this.calories,
            protein = this.protein,
            fat = this.fat,
            carbohydrates = this.carbohydrates
        )
    }
}