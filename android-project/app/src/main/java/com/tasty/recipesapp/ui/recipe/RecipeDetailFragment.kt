package com.tasty.recipesapp.ui.recipe

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tasty.recipesapp.Models.RecipeModel
import com.tasty.recipesapp.databinding.FragmentRecipeDetailBinding

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipeListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("RecipeDetailFragment", "Fragment created")

        viewModel.randomRecipe.observe(viewLifecycleOwner) { recipe ->
            Log.d("RecipeDetailFragment", "Received recipe: ${recipe?.name}")
            recipe?.let {
                displayRecipeDetails(it)
            }
        }
    }

    private fun displayRecipeDetails(recipe: RecipeModel) {
        try {
            binding.apply {
                titleText.text = recipe.name
                descriptionText.text = recipe.description

                nutritionText.text = """
                    Calories: ${recipe.nutrition.calories}
                    Protein: ${recipe.nutrition.protein}g
                    Fat: ${recipe.nutrition.fat}g
                    Carbs: ${recipe.nutrition.carbohydrates}g
                """.trimIndent()

                ingredientsText.text = recipe.components.joinToString("\n") {
                    "• ${it.rawText}"
                }

                instructionsText.text = recipe.instructions.joinToString("\n\n") {
                    "${it.position}. ${it.text}"
                }
            }
            Log.d("RecipeDetailFragment", "Recipe details displayed")
        } catch (e: Exception) {
            Log.e("RecipeDetailFragment", "Error displaying recipe: ${e.message}")
            e.printStackTrace() // Add this to get full error details
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}