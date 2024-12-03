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
import androidx.navigation.fragment.findNavController
import com.tasty.recipesapp.Models.*
import com.tasty.recipesapp.databinding.FragmentNewRecipeBinding
import com.tasty.recipesapp.databinding.ItemIngredientInputBinding
import com.tasty.recipesapp.databinding.ItemInstructionInputBinding
import java.io.ByteArrayOutputStream
import kotlin.math.sqrt
import com.tasty.recipesapp.Respository.ProfileViewModel

class NewRecipeFragment : Fragment() {

    private var _binding: FragmentNewRecipeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by activityViewModels()

    private var selectedImageUri: Uri? = null

    // Permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            Toast.makeText(context, "Permission needed for image selection", Toast.LENGTH_SHORT).show()
        }
    }

    // Image picker launcher
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.recipeImageView.setImageURI(it)
            binding.selectImageButton.text = ""
        }
    }

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
        setupImagePicker()
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

    private fun setupImagePicker() {
        binding.selectImageButton.setOnClickListener {
            checkPermissionAndPickImage()
        }

        binding.recipeImageView.setOnClickListener {
            checkPermissionAndPickImage()
        }
    }

    private fun checkPermissionAndPickImage() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        openImagePicker()
                    }
                    else -> {
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                }
            }
            else -> {
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        openImagePicker()
                    }
                    else -> {
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
        }
    }

    private fun openImagePicker() {
        pickImage.launch("image/*")
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

        // Set step number
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
                com.tasty.recipesapp.R.id.stepNumberText
            )
            stepNumberText.text = "${i + 1}."
        }
    }

    private fun compressAndConvertImageToBase64(uri: Uri): String {
        return try {
            binding.imageLoadingProgress.visibility = View.VISIBLE

            val inputStream = requireContext().contentResolver.openInputStream(uri)
            var bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Compress image if it's too large
            if (bitmap.byteCount > 1024 * 1024) { // If larger than 1MB
                val ratio = sqrt(1024 * 1024.0 / bitmap.byteCount)
                val width = (bitmap.width * ratio).toInt()
                val height = (bitmap.height * ratio).toInt()
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
            }

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e("NewRecipeFragment", "Error compressing image: ${e.message}")
            ""
        } finally {
            binding.imageLoadingProgress.visibility = View.GONE
        }
    }

    private fun saveRecipe() {
        try {
            // Collect basic information
            val name = binding.recipeNameInput.text.toString()
            val description = binding.recipeDescriptionInput.text.toString()
            val servings = binding.servingsInput.text.toString().toIntOrNull() ?: 0

            // Validate basic fields
            if (name.isBlank() || description.isBlank() || servings == 0) {
                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return
            }

            // Collect ingredients
            val ingredients = mutableListOf<ComponentModel>()
            for (i in 0 until binding.ingredientsContainer.childCount) {
                val ingredientView = binding.ingredientsContainer.getChildAt(i)
                val ingredientInput = ingredientView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                    com.tasty.recipesapp.R.id.ingredientInput
                )
                val amountInput = ingredientView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                    com.tasty.recipesapp.R.id.amountInput
                )

                val ingredient = ingredientInput.text.toString()
                val amount = amountInput.text.toString()

                if (ingredient.isNotBlank() && amount.isNotBlank()) {
                    ingredients.add(
                        ComponentModel(
                            rawText = "$amount of $ingredient",
                            ingredient = ingredient,
                            amount = amount,
                            position = i + 1
                        )
                    )
                }
            }

            // Collect instructions
            val instructions = mutableListOf<InstructionModel>()
            for (i in 0 until binding.instructionsContainer.childCount) {
                val instructionView = binding.instructionsContainer.getChildAt(i)
                val instructionInput = instructionView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                    com.tasty.recipesapp.R.id.instructionInput
                )

                val instructionText = instructionInput.text.toString()
                if (instructionText.isNotBlank()) {
                    instructions.add(
                        InstructionModel(
                            id = i + 1,
                            text = instructionText,
                            position = i + 1
                        )
                    )
                }
            }

            // Collect nutrition information
            val calories = binding.caloriesInput.text.toString().toIntOrNull() ?: 0
            val protein = binding.proteinInput.text.toString().toIntOrNull() ?: 0
            val fat = binding.fatInput.text.toString().toIntOrNull() ?: 0
            val carbs = binding.carbsInput.text.toString().toIntOrNull() ?: 0

            // Convert image to Base64 if selected
            val imageString = selectedImageUri?.let { uri ->
                try {
                    compressAndConvertImageToBase64(uri)
                } catch (e: Exception) {
                    Log.e("NewRecipeFragment", "Error converting image: ${e.message}")
                    ""
                }
            } ?: ""

            // Create recipe model
            val recipe = RecipeModel(
                id = System.currentTimeMillis().toInt(),
                name = name,
                description = description,
                thumbnailUrl = imageString,
                servings = servings,
                components = ingredients,
                instructions = instructions,
                nutrition = NutritionModel(
                    calories = calories,
                    protein = protein,
                    fat = fat,
                    carbohydrates = carbs
                ),
                isFavorite = false
            )

            // Save recipe
            //viewModel.addRecipe(recipe)
            Toast.makeText(context, "Recipe saved successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()

        } catch (e: Exception) {
            Log.e("NewRecipeFragment", "Error saving recipe: ${e.message}")
            Toast.makeText(context, "Error saving recipe: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}