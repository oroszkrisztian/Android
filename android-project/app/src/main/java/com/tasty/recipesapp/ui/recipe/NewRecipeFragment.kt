package com.tasty.recipesapp.ui.recipe

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tasty.recipesapp.R

class NewRecipeFragment : Fragment(R.layout.fragment_new_recipe) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_recipe, container, false)
    }
}
