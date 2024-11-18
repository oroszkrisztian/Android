package com.tasty.recipesapp.ui.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tasty.recipesapp.R
import com.tasty.recipesapp.databinding.FragmentRecipesBinding
import com.tasty.recipesapp.ui.recipe.adapter.RecipeAdapter

class RecipesFragment : Fragment() {

    private val viewModel: RecipeListViewModel by activityViewModels()
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
            setupRandomButton()
            observeViewModel()
        } catch (e: Exception) {
            Log.e("RecipesFragment", "Error in onViewCreated", e)
            // Handle error gracefully
            Toast.makeText(context, "Error loading recipes: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRandomButton() {
        binding.randomRecipeButton.setOnClickListener {
            viewModel.selectRandomRecipe()
            findNavController().navigate(R.id.action_recipesFragment_to_recipeDetailFragment)
        }
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter { recipe ->
            // nav based on id
            val directions = RecipesFragmentDirections
                .actionRecipesFragmentToRecipeDetailFragment(recipeId = recipe.id)
            findNavController().navigate(directions)
        }

        binding.recyclerView.apply {
            adapter = recipeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.updateRecipes(recipes)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
