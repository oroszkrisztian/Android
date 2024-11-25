package com.tasty.recipesapp.ui.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tasty.recipesapp.R
import com.tasty.recipesapp.databinding.FragmentRecipesBinding
import com.tasty.recipesapp.ui.recipe.adapter.RecipeAdapter

class RecipesFragment : Fragment() {

    private val viewModel: ProfileViewModel by activityViewModels()
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            _binding = FragmentRecipesBinding.inflate(inflater, container, false)
            return binding.root
        } catch (e: Exception) {
            Log.e("RecipesFragment", "Error in onCreateView", e)
            throw e
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            super.onViewCreated(view, savedInstanceState)
            setupRecyclerView()
            observeViewModel()
            binding.addRecipeButton.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_newRecipeFragment)
            }
        } catch (e: Exception) {
            Log.e("RecipesFragment", "Error in onViewCreated", e)
            Toast.makeText(context, "Error loading recipes: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter(
            onItemClick = { recipe ->
                val directions = RecipesFragmentDirections
                    .actionRecipesFragmentToRecipeDetailFragment(recipeId = recipe.id)
                findNavController().navigate(directions)
            },
            onFavoriteClick = { recipe ->
                viewModel.toggleFavorite(recipe)
            },
            onDeleteClick = { recipe ->
                viewModel.deleteRecipe(recipe)
            }
        )

        binding.recyclerView.apply {
            adapter = recipeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    private fun observeViewModel() {
        // Use myRecipes from ProfileViewModel instead of recipes
        viewModel.myRecipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.updateRecipes(recipes)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // You might want to handle errors differently in ProfileViewModel
        // This assumes you have similar error handling in ProfileViewModel
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e("RecipesFragment", "Error occurred: $it")
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}