package com.tasty.recipesapp.dao

import androidx.room.*
import com.tasty.recipesapp.Entity.RecipeEntity

@Dao
interface RecipeDao {
    @Insert
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Query("SELECT * FROM recipe WHERE internalId = :id")
    suspend fun getRecipeById(id: Long): RecipeEntity?

    @Query("SELECT * FROM recipe")
    suspend fun getAllRecipes(): List<RecipeEntity>

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("UPDATE recipe SET json = :json WHERE internalId = :id")
    suspend fun updateRecipe(id: Long, json: String)
}