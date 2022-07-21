package com.example.ep3_devops_faith.ui.post.create

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.ep3_devops_faith.R
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.databinding.FragmentAddPostBinding
import com.example.ep3_devops_faith.login.CredentialsManager
import timber.log.Timber

private const val pickImage = 100
class AddPostFragment : Fragment() {
    private lateinit var binding: FragmentAddPostBinding
    lateinit var viewModel: AddPostViewModel
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater, R.layout.fragment_add_post, container, false
            )

        // get an instance of the appContext to setup the database
        val appContext = requireNotNull(this.activity).application
        val dataSource = FaithDatabase.getInstance(appContext).postDatabaseDao

        // use a factory to pass the database reference to the viewModel
        val viewModelFactory = AddPostViewModelFactory(dataSource, appContext)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AddPostViewModel::class.java)
        binding.viewModel = viewModel
        // this call allows to automatically update the livedata
        // Meaning: no more resets or whatsoever
        binding.lifecycleOwner = this

        setClickListeners()

        viewModel.saveEvent.observe(viewLifecycleOwner, { saveEvent ->
            if (saveEvent) {
                Timber.i("REQUESTING TO SAVE A POST")
                var bitmap: Bitmap? = null
                when {
                    binding.imgPost.drawable != null -> {
                        bitmap = (binding.imgPost.drawable as BitmapDrawable).bitmap
                    }
                }
                SavePost(bitmap)
                viewModel.saveEventDone()
                Timber.i("POST HAS BEEN SAVED")
                view?.findNavController()
                    ?.navigate(AddPostFragmentDirections.actionAddPostFragmentToPostOverviewFragment())
            }
        })
        return binding.root
    }

    private fun SavePost(bitmap: Bitmap?) {
        viewModel.savePost(
            binding.editTextTextPost.text.toString(),
            bitmap,
            binding.LinkTextPost.text.toString(),
            CredentialsManager.cachedUserProfile?.getId()!!
        )
    }

    private fun setClickListeners() {
        binding.btnAddPicture.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.type = "image/*"
        requireActivity().startActivityFromFragment(this, gallery, pickImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            binding.imgPost.setImageURI(imageUri)
            Timber.d(data.toString())
        }
    }
}