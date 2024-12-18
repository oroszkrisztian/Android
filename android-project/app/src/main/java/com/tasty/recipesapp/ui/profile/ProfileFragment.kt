package com.tasty.recipesapp.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tasty.recipesapp.databinding.FragmentProfileBinding

import com.tasty.recipesapp.Respository.ProfileViewModel
import com.tasty.recipesapp.ui.recipe.adapter.RecipeListAdapter


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()
    private lateinit var recipeAdapter: RecipeListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        viewModel.loadFavorites()  // Make sure favorites are loaded
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeListAdapter(
            onFavoriteClick = { recipe ->
                viewModel.toggleFavorite(recipe)
            },
            onItemClick = { recipe ->
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToRecipeDetailFragment(recipe.recipeID)
                )
            },
            onDeleteClick = { recipe ->
                viewModel.toggleFavorite(recipe)
            }
        )
        binding.recyclerView.apply {
            adapter = recipeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            recipeAdapter.updateRecipes(favorites)
            recipeAdapter.updateFavorites(favorites)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}