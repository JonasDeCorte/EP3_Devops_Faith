package com.example.ep3_devops_faith.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.management.ManagementException
import com.auth0.android.management.UsersAPIClient
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.example.ep3_devops_faith.R
import com.example.ep3_devops_faith.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {
    private lateinit var account: Auth0
    private lateinit var binding: FragmentLoginBinding
    private var cachedCredentials: Credentials? = null
    private var cachedUserProfile: UserProfile? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Set up the account object with the Auth0 application details
        account = Auth0(
            getString(R.string.authClientId),
            getString(R.string.authDomain)
        )
        // Bind the button click with the login action
        binding = FragmentLoginBinding.inflate(layoutInflater)
        binding.buttonLogin.setOnClickListener { loginWithBrowser() }
        binding.buttonLogout.setOnClickListener { logout() }
        binding.buttonGetMetadata.setOnClickListener { getUserMetadata() }
        binding.buttonPatchMetadata.setOnClickListener { patchUserMetadata() }
        binding.btnHome.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_homeFragment)
        )
        return binding.root
    }
private fun updateUI() {
    binding.buttonLogout.isEnabled = cachedCredentials != null
    binding.metadataPanel.isVisible = cachedCredentials != null
    binding.buttonLogin.isEnabled = cachedCredentials == null
    binding.userProfile.isVisible = cachedCredentials != null

    binding.userProfile.text =
        "Name: ${cachedUserProfile?.name ?: ""}\n" +
                "Email: ${cachedUserProfile?.email ?: ""}"

    if (cachedUserProfile == null) {
        binding.inputEditMetadataName.setText("")
        binding.inputEditMetadataRole.setText("")
    }
}

private fun loginWithBrowser() {
    // Setup the WebAuthProvider, using the custom scheme and scope.
    WebAuthProvider.login(account)
        .withScheme(getString(R.string.com_auth0_scheme))
        .withScope("openid profile email read:current_user update:current_user_metadata")
        .withAudience("https://${getString(R.string.authDomain)}/api/v2/")

        // Launch the authentication passing the callback where the results will be received
        .start(requireContext(), object : Callback<Credentials, AuthenticationException> {
            override fun onFailure(exception: AuthenticationException) {
                showSnackBar("Failure: ${exception.getCode()}")
            }
            override fun onSuccess(credentials: Credentials) {
                cachedCredentials = credentials
                showSnackBar("Success: ${credentials.accessToken}")
                updateUI()
                showUserProfile()
            }
        })
}

private fun logout() {
    WebAuthProvider.logout(account)
        .withScheme(getString(R.string.com_auth0_scheme))
        .start(requireContext(), object : Callback<Void?, AuthenticationException> {
            override fun onSuccess(payload: Void?) {
                // The user has been logged out!
                cachedCredentials = null
                cachedUserProfile = null
                updateUI()
            }
            override fun onFailure(exception: AuthenticationException) {
                updateUI()
                showSnackBar("Failure: ${exception.getCode()}")
            }
        })
}

private fun showUserProfile() {
    val client = AuthenticationAPIClient(account)

    // Use the access token to call userInfo endpoint.
    // We can assume cachedCredentials has been initialized by this point.
    client.userInfo(cachedCredentials!!.accessToken)
        .start(object : Callback<UserProfile, AuthenticationException> {
            override fun onFailure(exception: AuthenticationException) {
                showSnackBar("Failure: ${exception.getCode()}")
            }
            override fun onSuccess(profile: UserProfile) {
                cachedUserProfile = profile
                updateUI()
            }
        })
}

private fun getUserMetadata() {
    // Create the user API client
    val usersClient = UsersAPIClient(account, cachedCredentials!!.accessToken)

    // Get the full user profile
    usersClient.getProfile(cachedUserProfile!!.getId()!!)
        .start(object : Callback<UserProfile, ManagementException> {
            override fun onFailure(exception: ManagementException) {
                showSnackBar("Failure: ${exception.getCode()}")
            }
            override fun onSuccess(userProfile: UserProfile) {
                cachedUserProfile = userProfile
                updateUI()
                val Name = userProfile.getUserMetadata()["Name"] as String?
                binding.inputEditMetadataName.setText(Name)
                val Role = userProfile.getUserMetadata()["Role"] as String?
                binding.inputEditMetadataRole.setText(Role)
            }
        })
}

private fun patchUserMetadata() {
    val usersClient = UsersAPIClient(account, cachedCredentials!!.accessToken)
    val metadata = mapOf("Name" to binding.inputEditMetadataName.text.toString().trim(),
        "Role" to binding.inputEditMetadataRole.text.toString().trim())
    usersClient
        .updateMetadata(cachedUserProfile!!.getId()!!, metadata)
        .start(object : Callback<UserProfile, ManagementException> {
            override fun onFailure(exception: ManagementException) {
                showSnackBar("Failure: ${exception.getCode()}")
            }

            override fun onSuccess(profile: UserProfile) {
                cachedUserProfile = profile
                updateUI()
                showSnackBar("Successful")
            }
        })
}

private fun showSnackBar(text: String) {
    Snackbar.make(
        binding.root,
        text,
        Snackbar.LENGTH_LONG
    ).show()
}
}