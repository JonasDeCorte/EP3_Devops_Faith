package com.example.ep3_devops_faith.ui.user.profile

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.auth0.android.Auth0
import com.auth0.android.callback.Callback
import com.auth0.android.management.ManagementException
import com.auth0.android.management.UsersAPIClient
import com.auth0.android.result.UserProfile
import com.example.ep3_devops_faith.R
import com.example.ep3_devops_faith.databinding.FragmentUserProfileBinding
import com.example.ep3_devops_faith.login.CredentialsManager
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
// TODO refactor into viewModel?
private const val pickImage = 100
class UserProfileFragment : Fragment() {
    private lateinit var binding: FragmentUserProfileBinding
    lateinit var viewModel: UserProfileViewModel
    private lateinit var account: Auth0
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater, R.layout.fragment_user_profile, container, false
            )
        account = Auth0(
            getString(R.string.authClientId),
            getString(R.string.authDomain)
        )
        // get an instance of the appContext to setup the database
        val appContext = requireNotNull(this.activity).application

        // use a factory to pass the database reference to the viewModel
        val viewModelFactory = UserProfileViewModelFactory(appContext)
        viewModel = ViewModelProvider(this, viewModelFactory).get(UserProfileViewModel::class.java)
        binding.viewModel = viewModel
        // this call allows to automatically update the livedata
        // Meaning: no more resets or whatsoever
        binding.lifecycleOwner = this

        setClickListeners()
        getUserMetadata()
        viewModel.saveEvent.observe(viewLifecycleOwner, { saveEvent ->
            if (saveEvent) {
                Timber.i("REQUESTING TO SAVE A USER PROFILE")
                patchUserMetadata()
                viewModel.saveEventDone()
                Timber.i("USER PROFILE HAS BEEN SAVED")
            }
        })
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun setClickListeners() {
        binding.btnAddPicture.setOnClickListener {
            openGallery()
        }
        binding.btnSave.setOnClickListener {
            patchUserMetadata()
        }
    }

    private fun openGallery() {
        val gallery =
            Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.type = "image/*"
        requireActivity().startActivityFromFragment(this, gallery, pickImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            binding.imgProfile.setImageURI(imageUri)
            binding.imgProfile.tag = data?.data.toString()
            Timber.d(data?.data.toString())
        }
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
                    updateUI()
                    val name = userProfile.getUserMetadata()["Name"] as String?
                    binding.txtName.setText(name)
                    val role = userProfile.getUserMetadata()["Role"] as String?
                    binding.txtRole.setText(role)
                    val avatar = userProfile.getUserMetadata()["Picture_url"] as String?
                    if (avatar != null) {
                        binding.imgProfile.setImageURI(Uri.parse(avatar))
                    }
                }
            })
    }

    private fun patchUserMetadata() {
        if (CredentialsManager.cachedCredentials == null) {
            return
        }
        val usersClient =
            UsersAPIClient(account, CredentialsManager.cachedCredentials!!.accessToken)
        Timber.i("usersClient: $usersClient")
        var bitmap: Bitmap? = null
        when {
            binding.imgProfile.drawable != null -> {
                bitmap = (binding.imgProfile.drawable as BitmapDrawable).bitmap
            }
        }
        var metadata: Map<String, String>
        if (binding.imgProfile.tag != null) {
            metadata = mapOf(
                "Name" to binding.txtName.text.toString().trim(),
                "Role" to binding.txtRole.text.toString().trim(),
                "Picture_url" to binding.imgProfile.tag.toString()
            )
        } else {
            metadata = mapOf(
                "Name" to binding.txtName.text.toString().trim(),
                "Role" to binding.txtRole.text.toString().trim())
        }

        usersClient
            .updateMetadata(CredentialsManager.cachedUserProfile!!.getId()!!, metadata)
            .start(object : Callback<UserProfile, ManagementException> {
                override fun onFailure(exception: ManagementException) {
                    showSnackBar("Failure: ${exception.getCode()}")
                }

                override fun onSuccess(profile: UserProfile) {
                    CredentialsManager.cachedUserProfile = profile
                    Timber.i("Profile: $profile")
                    updateUI()
                    showSnackBar("Successful")
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        binding.txtRole.isVisible = CredentialsManager.cachedCredentials != null
        binding.txtName.isVisible = CredentialsManager.cachedCredentials != null
        binding.btnAddPicture.isVisible = CredentialsManager.cachedCredentials != null
        binding.btnSave.isVisible = CredentialsManager.cachedCredentials != null

        if (CredentialsManager.cachedUserProfile == null) {
            binding.txtName.setText("")
            binding.txtRole.setText("")
        }
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
        val role = CredentialsManager.cachedUserProfile!!.getUserMetadata().get("Role") as String?
        if (!role.equals(getString(R.string.Type_Jongere))) {
            menu.findItem(R.id.favoritePostsOverViewFragment).setVisible(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()) ||
                super.onOptionsItemSelected(item)
    }
}