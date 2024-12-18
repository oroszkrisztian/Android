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
    private val onFavoriteClick: (ApiRecipeDTO) -> Unit,
    private val onItemClick: (ApiRecipeDTO) -> Unit,
    private val onDeleteClick: (ApiRecipeDTO) -> Unit
) : RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>() {
    private var recipes = listOf<ApiRecipeDTO>()
    private var favoriteIds = setOf<Int>()
    private var showDeleteButton = false

    fun updateRecipes(newRecipes: List<ApiRecipeDTO>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }

    fun updateFavorites(favorites: List<ApiRecipeDTO>) {
        favoriteIds = favorites.map { it.recipeID }.toSet()
        notifyDataSetChanged()
    }

    fun setShowDeleteButton(show: Boolean) {
        showDeleteButton = show
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

                favoriteButton.text = if (favoriteIds.contains(recipe.recipeID)) "Liked" else "Like"
                favoriteButton.setOnClickListener {
                    onFavoriteClick(recipe)
                }

                deleteButton.visibility = if (showDeleteButton) View.VISIBLE else View.GONE
                deleteButton.setOnClickListener {
                    onDeleteClick(recipe)
                }

                root.setOnClickListener {
                    onItemClick(recipe)
                }
            }
        }
    }
}