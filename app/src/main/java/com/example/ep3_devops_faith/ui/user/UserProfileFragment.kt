package com.example.ep3_devops_faith.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.ep3_devops_faith.R
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.databinding.FragmentUserProfileBinding
/**
 *  A simple [Fragment] subclass.
 */
class UserProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentUserProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_user_profile, container, false
        )
        // Get an instance of the appContext to setup the database
        val application = requireNotNull(this.activity).application
        val dataSource = FaithDatabase.getInstance(application).userDatabaseDao
        // use a factory to pass the database reference to the viewModel
        val viewModelFactory = UserViewModelFactory(dataSource, application)
        val userProfileViewModel =
            ViewModelProvider(this, viewModelFactory).get(UserProfileViewModel::class.java)
        binding.userProfileViewModel = userProfileViewModel
        // this call allows to automatically update the livedata
        // Meaning: no more resets or whatsoever
        binding.setLifecycleOwner(this)
        userProfileViewModel.saveEvent.observe(viewLifecycleOwner, Observer { saveEvent ->
            if (saveEvent) {
                userProfileViewModel.saveProfile(
                    binding.txtName.text.toString(),
                    binding.txtRole.text.toString()
                )
                // navigate back to the home screen
                view?.findNavController()
                    ?.navigate(UserProfileFragmentDirections.actionUserProfileFragmentToHomeFragment())
                userProfileViewModel.saveEventDone()
            }
        })
        return binding.root
    }
}