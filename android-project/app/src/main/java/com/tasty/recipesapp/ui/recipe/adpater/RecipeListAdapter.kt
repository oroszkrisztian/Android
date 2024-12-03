package com.tasty.recipesapp.ui.recipe.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64  // Only this Base64 import
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tasty.recipesapp.Models.RecipeModel
import com.tasty.recipesapp.api.dto.ApiRecipeDTO
import com.tasty.recipesapp.databinding.ItemRecipeBinding
import com.tasty.recipesapp.databinding.ItemRecipeBinding.*

class RecipeListAdapter(
    private val onFavoriteClick: (ApiRecipeDTO) -> Unit
) : RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>() {
    private var recipes = listOf<ApiRecipeDTO>()
    private var favoriteIds = setOf<Int>()

    fun updateRecipes(newRecipes: List<ApiRecipeDTO>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount() = recipes.size

    fun updateFavorites(favorites: List<ApiRecipeDTO>) {
        favoriteIds = favorites.map { it.recipeID }.toSet()
        Log.d("com.tasty.recipeapp", "Updated favorite IDs: $favoriteIds")
        notifyDataSetChanged()
    }

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: ApiRecipeDTO) {
            binding.apply {
                recipeName.text = recipe.name
                recipeDescription.text = recipe.description
                servingsInfo.text = "Serves ${recipe.numServings}"

                Glide.with(itemView.context)
                    .load(recipe.thumbnailUrl)
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(recipeImage)

                val isFavorited = favoriteIds.contains(recipe.recipeID)
                Log.d("com.tasty.recipeapp", "Recipe ${recipe.recipeID} is favorited: $isFavorited")
                favoriteButton.text = if (isFavorited) "Liked" else "Like"
                favoriteButton.setOnClickListener {
                    onFavoriteClick(recipe)
                }
            }
        }
    }

}