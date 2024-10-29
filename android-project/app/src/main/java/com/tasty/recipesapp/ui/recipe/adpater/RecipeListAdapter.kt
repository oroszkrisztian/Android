package com.tasty.recipesapp.ui.recipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tasty.recipesapp.Models.RecipeModel

import com.tasty.recipesapp.databinding.ItemRecipeBinding

class RecipeAdapter(
    private var recipes: List<RecipeModel> = emptyList(),
    private val onItemClick: (RecipeModel) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    fun updateRecipes(newRecipes: List<RecipeModel>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    class RecipeViewHolder(
        private val binding: ItemRecipeBinding,
        private val onItemClick: (RecipeModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: RecipeModel) {
            binding.apply {
                recipeName.text = recipe.name
                recipeDescription.text = recipe.description
                servingsInfo.text = "Serves ${recipe.servings}"
                calories.text = "${recipe.nutrition.calories} calories"

                // Set click listener
                root.setOnClickListener { onItemClick(recipe) }
            }
        }
    }
}