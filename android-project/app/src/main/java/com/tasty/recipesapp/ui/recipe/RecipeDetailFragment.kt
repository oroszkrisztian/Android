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
import com.tasty.recipesapp.Models.RecipeModel
import com.tasty.recipesapp.databinding.FragmentRecipeDetailBinding

class RecipeDetailFragment : Fragment() {

    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!
    private val args: RecipeDetailFragmentArgs by navArgs()
    private val viewModel: ProfileViewModel by activityViewModels()

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

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

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
                // Display recipe image
                if (recipe.thumbnailUrl.isNotEmpty()) {
                    try {
                        val imageBytes = Base64.decode(recipe.thumbnailUrl, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        recipeImage.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        Log.e("RecipeDetailFragment", "Error loading image: ${e.message}")
                        recipeImage.setImageResource(android.R.drawable.ic_menu_gallery)
                    }
                } else {
                    recipeImage.setImageResource(android.R.drawable.ic_menu_gallery)
                }

                // Basic Info Card
                titleText.text = recipe.name
                servingsText.text = "Serves ${recipe.servings}"
                descriptionText.text = recipe.description

                // Nutrition Card
                nutritionText.text = """
                Calories: ${recipe.nutrition.calories}
                Protein: ${recipe.nutrition.protein}g
                Fat: ${recipe.nutrition.fat}g
                Carbs: ${recipe.nutrition.carbohydrates}g
            """.trimIndent()

                // Ingredients Card
                ingredientsText.text = recipe.components.joinToString("\n") {
                    "• ${it.amount} of ${it.ingredient}"
                }

                // Instructions Card
                instructionsText.text = recipe.instructions.joinToString("\n\n") {
                    "${it.position}. ${it.text}"
                }
            }
            Log.d("RecipeDetailFragment", "Recipe details displayed")
        } catch (e: Exception) {
            Log.e("RecipeDetailFragment", "Error displaying recipe: ${e.message}")
            e.printStackTrace()
            Toast.makeText(context, "Error displaying recipe", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}