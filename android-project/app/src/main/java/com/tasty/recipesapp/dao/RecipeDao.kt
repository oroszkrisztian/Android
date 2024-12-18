package com.tasty.recipesapp.dao

import androidx.room.*
import com.tasty.recipesapp.Entity.RecipeEntity

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity): Long

    @Transaction
    @Query("SELECT * FROM recipe")
    suspend fun getAllRecipes(): List<RecipeEntity>

    @Query("SELECT COUNT(*) FROM recipe")
    suspend fun getRecipeCount(): Int

    @Transaction
    @Query("SELECT * FROM recipe WHERE recipe_id = :apiId")
    suspend fun getRecipeByApiId(apiId: Int): RecipeEntity?

    @Query("DELETE FROM recipe WHERE recipe_id = :apiId")
    suspend fun removeFromFav(apiId: Int)
}