package com.tasty.recipesapp.ui.recipe

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.tasty.recipesapp.Models.RecipeModel
import com.tasty.recipesapp.databinding.FragmentRecipeDetailBinding

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!
    private val args: RecipeDetailFragmentArgs by navArgs()
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

        // Get recipe by ID
        viewModel.getRecipeById(args.recipeId)

        // Observe selected recipe
        viewModel.selectedRecipe.observe(viewLifecycleOwner) { recipe ->
            recipe?.let {
                displayRecipeDetails(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
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