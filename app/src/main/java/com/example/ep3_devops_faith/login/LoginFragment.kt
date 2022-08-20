package com.example.ep3_devops_faith.login
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import timber.log.Timber
class LoginFragment : Fragment() {
    private lateinit var account: Auth0
    private lateinit var binding: FragmentLoginBinding
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

        setClickListeners()
        checkIfToken()
        return binding.root
    }
    private fun setClickListeners() {
        binding.buttonLogin.setOnClickListener { loginWithBrowser() }
        binding.buttonLogout.setOnClickListener { logout() }
    }
    private fun checkIfToken() {
        val token = CredentialsManager.getAccessToken(requireContext())
        if (token != null) {
            // checking if the token works...
            showUserProfile()
        } else {
            Toast.makeText(context, "Token doesn't exist", Toast.LENGTH_SHORT).show()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        binding.buttonLogout.isEnabled = CredentialsManager.cachedCredentials != null
        binding.metadataPanel.isVisible = CredentialsManager.cachedCredentials != null
        binding.buttonLogin.isEnabled = CredentialsManager.cachedCredentials == null
        binding.userProfile.isVisible = CredentialsManager.cachedCredentials != null
        binding.userProfile.text =
            "Name: ${CredentialsManager.cachedUserProfile?.name ?: ""}\n" +
                    "Email: ${CredentialsManager.cachedUserProfile?.email ?: ""}"
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
                CredentialsManager.cachedCredentials = credentials
                CredentialsManager.saveCredentials(requireContext(), credentials)
                showSnackBar("Success: ${credentials.accessToken}")
                checkIfToken()
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
                CredentialsManager.cachedCredentials = null
                CredentialsManager.cachedUserProfile = null
                updateUI()
            }
            override fun onFailure(exception: AuthenticationException) {
                updateUI()
                showSnackBar("Failure: ${exception.getCode()}")
            }
        })
}
    private fun showUserProfile() {
        if (CredentialsManager.cachedCredentials == null) {
            return
        }
        // The line below creates an instance of AuthenticationAPIClient,
        // an object for contacting the Auth0 API for account information.
        // This object will be used to request the user’s profile information.
        val client = AuthenticationAPIClient(account)
        // Use the access token to call userInfo endpoint.
        // We can assume cachedCredentials has been initialized by this point.
        client.userInfo(CredentialsManager.cachedCredentials!!.accessToken)
            .start(object : Callback<UserProfile, AuthenticationException> {
                override fun onFailure(exception: AuthenticationException) {
                    showSnackBar("Failure: ${exception.getCode()}")
                }
                override fun onSuccess(profile: UserProfile) {
                    CredentialsManager.cachedUserProfile = profile
                    Timber.i("credentialsLogin: ${CredentialsManager.cachedUserProfile!!.getUserMetadata()}")
                    updateUI()
                    getUserMetadata()
                }
            })
    }
    private fun getUserMetadata() {
        if (CredentialsManager.cachedCredentials == null || CredentialsManager.cachedUserProfile == null) {
            return
        }
        // Create the user API client
        val usersClient =
            UsersAPIClient(account, CredentialsManager.cachedCredentials!!.accessToken)
        Timber.i("Userclient: $usersClient")
        // Get the full user profile
        usersClient.getProfile(CredentialsManager.cachedUserProfile!!.getId()!!)
            .start(object : Callback<UserProfile, ManagementException> {
                override fun onFailure(exception: ManagementException) {
                    showSnackBar("Failure: ${exception.getCode()}")
                }
                override fun onSuccess(userProfile: UserProfile) {
                    CredentialsManager.cachedUserProfile = userProfile
                    Timber.i("UserProfile: ${userProfile.getUserMetadata()}")
                    Timber.i("credentials: ${CredentialsManager.cachedUserProfile!!.getUserMetadata()}")
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