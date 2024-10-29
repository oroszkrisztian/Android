package com.tasty.recipesapp.ui.recipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.tasty.recipesapp.R

class RecipesFragment : Fragment() {

    private val viewModel: RecipeListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe recipes
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipes.forEach { recipe ->
                Log.d("RecipesFragment", """
                    Recipe loaded:
                    Name: ${recipe.name}
                    Description: ${recipe.description}
                    Components: ${recipe.components.size}
                    Instructions: ${recipe.instructions.size}
                    Nutrition:
                    - Calories: ${recipe.nutrition.calories}
                    - Protein: ${recipe.nutrition.protein}g
                """.trimIndent())
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            Log.d("RecipesFragment", "Loading: $isLoading")
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }
}