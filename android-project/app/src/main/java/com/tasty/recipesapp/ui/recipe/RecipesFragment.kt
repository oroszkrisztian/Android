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
import com.tasty.recipesapp.Respository.ProfileViewModel
import com.tasty.recipesapp.databinding.FragmentRecipesBinding
import com.tasty.recipesapp.ui.recipe.adapter.RecipeListAdapter

class RecipesFragment : Fragment() {
    private val viewModel: ProfileViewModel by activityViewModels()
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!
    private lateinit var recipeAdapter: RecipeListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        viewModel.loadRecipes()
        viewModel.loadFavorites()

        binding.addRecipeButton.setOnClickListener {
            findNavController().navigate(
                RecipesFragmentDirections.actionRecipesFragmentToNewRecipeFragment()
            )
        }
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeListAdapter(
            onFavoriteClick = { recipe ->
                viewModel.toggleFavorite(recipe)
            },
            onItemClick = { recipe ->
                findNavController().navigate(
                    RecipesFragmentDirections.actionRecipesFragmentToRecipeDetailFragment(recipe.recipeID)
                )
            }
        )
        binding.recyclerView.apply {
            adapter = recipeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.updateRecipes(recipes)
        }

        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            recipeAdapter.updateFavorites(favorites)
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