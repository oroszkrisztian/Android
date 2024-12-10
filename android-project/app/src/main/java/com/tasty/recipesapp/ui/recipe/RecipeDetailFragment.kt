package com.tasty.recipesapp.ui.recipe

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.tasty.recipesapp.Models.RecipeModel
import com.tasty.recipesapp.databinding.FragmentRecipeDetailBinding
import com.tasty.recipesapp.Respository.ProfileViewModel

class RecipeDetailFragment : Fragment() {
    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()
    private val args: RecipeDetailFragmentArgs by navArgs()

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
        loadRecipeDetails()
    }

    private fun loadRecipeDetails() {
        val recipeId = args.recipeId
        viewModel.getRecipeById(recipeId)

        viewModel.selectedRecipe.observe(viewLifecycleOwner) { recipe ->
            recipe?.let {
                binding.apply {
                    titleText.text = it.name
                    descriptionText.text = it.description
                    servingsText.text = "Serves ${it.numServings}"

                    // Load image with Glide
                    Glide.with(requireContext())
                        .load(it.thumbnailUrl)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(recipeImage)

                    // Nutrition information
                    nutritionText.text = recipe.nutrition?.let {
                        """
                            Calories: ${it.calories}
                            Protein: ${it.protein}g
                            Fat: ${it.fat}g
                            Carbohydrates: ${it.carbohydrates}g
                            Sugar: ${it.sugar}g
                            Fiber: ${it.fiber}g
                            """.trimIndent()
                    } ?: "Nutrition information not available"

                    // Display components/ingredients if available
                    if (it.components.isNotEmpty()) {
                        ingredientsText.text = it.components.joinToString("\n") { component ->
                            "• ${component.rawText}"
                        }
                    } else {
                        ingredientsText.text = "No ingredients listed"
                    }

                    // Display instructions if available
                    if (it.instructions.isNotEmpty()) {
                        instructionsText.text = it.instructions.joinToString("\n\n") { instruction ->
                            "${instruction.position}. ${instruction.displayText}"
                        }
                    } else {
                        instructionsText.text = "No instructions available"
                    }

                    // Add back button functionality
                    backButton.setOnClickListener {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}