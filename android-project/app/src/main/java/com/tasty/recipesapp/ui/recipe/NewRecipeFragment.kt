package com.tasty.recipesapp.ui.recipe

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.tasty.recipesapp.Models.*
import com.tasty.recipesapp.R
import com.tasty.recipesapp.databinding.FragmentNewRecipeBinding
import com.tasty.recipesapp.databinding.ItemIngredientInputBinding
import com.tasty.recipesapp.databinding.ItemInstructionInputBinding
import java.io.ByteArrayOutputStream
import kotlin.math.sqrt
import com.tasty.recipesapp.Respository.ProfileViewModel
import com.tasty.recipesapp.api.dto.ApiComponentDTO
import com.tasty.recipesapp.api.dto.ApiIngredientDTO
import com.tasty.recipesapp.api.dto.ApiInstructionDTO
import com.tasty.recipesapp.api.dto.ApiMeasurementDTO
import com.tasty.recipesapp.api.dto.ApiRecipeDTO
import com.tasty.recipesapp.api.dto.ApiUnitDTO
import com.tasty.recipesapp.auth.manager.TokenManager
import com.tasty.recipesapp.ui.home.HomeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class NewRecipeFragment : Fragment() {
    private var _binding: FragmentNewRecipeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupBackButton()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupButtons() {
        binding.apply {
            addIngredientButton.setOnClickListener {
                addIngredientInput()
            }
            addInstructionButton.setOnClickListener {
                addInstructionInput()
            }
            saveRecipeButton.setOnClickListener {
                saveRecipe()
            }
        }
    }

    private fun addIngredientInput() {
        val ingredientBinding = ItemIngredientInputBinding.inflate(
            layoutInflater,
            binding.ingredientsContainer,
            true
        )
        ingredientBinding.removeIngredientButton.setOnClickListener {
            binding.ingredientsContainer.removeView(ingredientBinding.root)
        }
    }

    private fun addInstructionInput() {
        val instructionBinding = ItemInstructionInputBinding.inflate(
            layoutInflater,
            binding.instructionsContainer,
            true
        )
        val stepNumber = binding.instructionsContainer.childCount
        instructionBinding.stepNumberText.text = "$stepNumber."

        instructionBinding.removeInstructionButton.setOnClickListener {
            binding.instructionsContainer.removeView(instructionBinding.root)
            updateInstructionNumbers()
        }
    }

    private fun updateInstructionNumbers() {
        for (i in 0 until binding.instructionsContainer.childCount) {
            val instructionView = binding.instructionsContainer.getChildAt(i)
            val stepNumberText = instructionView.findViewById<android.widget.TextView>(
                R.id.stepNumberText
            )
            stepNumberText.text = "${i + 1}."
        }
    }



    private fun saveRecipe() {
        try {
            // Check if we have a token
            if (TokenManager.getToken() == null) {
                Toast.makeText(context, "Please login first to add recipes", Toast.LENGTH_SHORT).show()
                return
            }

            // Get input values
            val name = binding.recipeNameInput.text.toString()
            val description = binding.recipeDescriptionInput.text.toString()
            val servings = binding.servingsInput.text.toString().toIntOrNull() ?: 0
            val keywords = binding.keywordsInput.text.toString()
            val imageUrl = binding.imageUrlInput.text.toString()

            // Validate required fields
            if (name.isBlank() || description.isBlank() || servings == 0) {
                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return
            }

            // Create recipe with positive ID
            val recipe = ApiRecipeDTO(
                recipeID = abs(System.currentTimeMillis().toInt()),
                name = name,
                description = description,
                thumbnailUrl = imageUrl,
                keywords = keywords,
                isPublic = true,
                userEmail = "orosz.krisztian@student.ms.sapientia.ro",
                originalVideoUrl = "",
                country = "RO",
                numServings = servings,
                components = collectIngredients(),
                instructions = collectInstructions(),
            )

            lifecycleScope.launch {
                try {
                    viewModel.addNewRecipe(recipe)

                    // Show success message and navigate back
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Recipe saved successfully", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("NewRecipeFragment", "Error saving recipe: ${e.message}")
                        Toast.makeText(context, "Error saving recipe: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("NewRecipeFragment", "Error preparing recipe: ${e.message}")
            Toast.makeText(context, "Error preparing recipe: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun collectIngredients(): List<ApiComponentDTO> {
        val ingredients = mutableListOf<ApiComponentDTO>()
        for (i in 0 until binding.ingredientsContainer.childCount) {
            val ingredientView = binding.ingredientsContainer.getChildAt(i)
            val ingredientInput = ingredientView.findViewById<TextInputEditText>(R.id.ingredientInput)
            val amountInput = ingredientView.findViewById<TextInputEditText>(R.id.amountInput)
            val unitInput = ingredientView.findViewById<TextInputEditText>(R.id.unitInput)

            val ingredient = ingredientInput.text.toString()
            val amount = amountInput.text.toString()
            val unit = unitInput.text.toString()

            if (ingredient.isNotBlank() && amount.isNotBlank()) {
                ingredients.add(
                    ApiComponentDTO(
                        rawText = "$amount ${unit.ifBlank { "" }} $ingredient",
                        ingredient = ApiIngredientDTO(name = ingredient),
                        measurement = ApiMeasurementDTO(
                            quantity = amount,
                            unit = ApiUnitDTO(
                                name = unit,
                                displaySingular = unit,
                                displayPlural = unit,
                                abbreviation = unit
                            )
                        ),
                        position = i + 1
                    )
                )
            }
        }
        return ingredients
    }

    private fun collectInstructions(): List<ApiInstructionDTO> {
        val instructions = mutableListOf<ApiInstructionDTO>()
        for (i in 0 until binding.instructionsContainer.childCount) {
            val instructionView = binding.instructionsContainer.getChildAt(i)
            val instructionInput = instructionView.findViewById<TextInputEditText>(R.id.instructionInput)

            val instructionText = instructionInput.text.toString()
            if (instructionText.isNotBlank()) {
                instructions.add(
                    ApiInstructionDTO(
                        instructionID = 0,
                        displayText = instructionText,
                        position = i + 1
                    )
                )
            }
        }
        return instructions
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}