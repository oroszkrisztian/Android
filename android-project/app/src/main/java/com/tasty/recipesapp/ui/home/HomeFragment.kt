package com.tasty.recipesapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.tasty.recipesapp.R
import com.tasty.recipesapp.auth.manager.TokenManager
import com.tasty.recipesapp.databinding.FragmentHome2Binding


class HomeFragment : Fragment() {
    private var _binding: FragmentHome2Binding? = null
    private val binding get() = _binding!!

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val _idToken = MutableLiveData<String?>()
    val idToken: LiveData<String?> = _idToken

    companion object {
        private const val REQ_ONE_TAP = 1
        private const val WEB_CLIENT_ID = "162589133748-qjgufs6rv44fcrt4q8dstre6v1elo4cs.apps.googleusercontent.com"
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHome2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeSignIn()
        setupRefreshButton()
        checkExistingToken()
    }

    private fun checkExistingToken() {
        if (TokenManager.hasToken()) {
            binding.tokenStatus.text = "Token exists"
        } else {
            binding.tokenStatus.text = "No token"
        }
    }

    private fun initializeSignIn() {
        oneTapClient = Identity.getSignInClient(requireActivity())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    private fun setupRefreshButton() {
        binding.refreshTokenButton.setOnClickListener {
            showGoogleSignIn()
        }
    }

    private fun showGoogleSignIn() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender,
                        REQ_ONE_TAP,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    Toast.makeText(context, "Sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Google Sign-In failed: ${e.localizedMessage}")
                Toast.makeText(context, "Sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_ONE_TAP) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                val email = credential.id

                if (idToken != null) {
                    Log.d(TAG, "Got ID Token: ${idToken.take(10)}...")
                    TokenManager.setToken(idToken)
                    _idToken.value = idToken
                    Toast.makeText(context, "Sign in successful", Toast.LENGTH_SHORT).show()
                    checkExistingToken()
                } else {
                    Log.e(TAG, "No ID Token found!")
                    Toast.makeText(context, "Failed to get token", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Sign-In failed: ${e.localizedMessage}")
                Toast.makeText(context, "Sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}