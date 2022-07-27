package com.example.ep3_devops_faith.ui.post.read

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ep3_devops_faith.R
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.databinding.FragmentPostOverviewBinding
import com.example.ep3_devops_faith.login.CredentialsManager
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class PostOverviewFragment : Fragment() {
    lateinit var binding: FragmentPostOverviewBinding
    private lateinit var postViewModel: PostOverviewViewModel
    private val role = CredentialsManager.cachedUserProfile!!.getUserMetadata().get("Role") as String?
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_post_overview, container, false)

        // setup the db connection
        val application = requireNotNull(this.activity).application
        val dataSource = FaithDatabase.getInstance(application)
        // create the factory + viewmodel
        val viewModelFactory = PostOverviewViewModelFactory(dataSource, application)
        postViewModel =
            ViewModelProvider(this, viewModelFactory)[PostOverviewViewModel::class.java]
        // Giving the binding access to the PostOverviewViewModel
        binding.postViewModel = postViewModel
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        // Sets the adapter of the PostAdapter RecyclerView with clickHandler lambda that
        // tells the viewModel when our property is clicked
        binding.postList.adapter = PostAdapter(PostListener {
            postViewModel.displayPropertyDetails(it)
        }, FavoriteListener {
            postViewModel.favoriteClick(it)
        })

        // Observe the navigateToSelectedProperty LiveData and Navigate when it isn't null
        // After navigating, call displayPropertyDetailsComplete() so that the ViewModel is ready
        // for another navigation event.

        postViewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, {
            if (null != it) {
                if (!role.equals("Jongere")) {
                    postViewModel.saveStatus(it)
                }
                // Must find the NavController from the Fragment
                this.findNavController().navigate(
                    PostOverviewFragmentDirections.actionPostOverviewFragmentToPostDetailFragment(it)
                )
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                postViewModel.displayPropertyDetailsComplete()
            }
        })
        postViewModel.event.observe(viewLifecycleOwner, { post ->
            if (null != post) {
                postViewModel.isFavorite(post).observe(viewLifecycleOwner, { isFav ->
                    if (isFav) {
                        postViewModel.removeFavorite(post)
                        showSnackBar("REMOVED POST FROM FAVORITES")
                        Timber.i("REMOVED POST FROM FAVORITES")
                        postViewModel.EventDone()
                    } else {
                        postViewModel.saveFavorite(post)
                        postViewModel.EventDone()
                        Timber.i(" SAVED POST TO FAVORITES")
                        showSnackBar("SAVED POST TO FAVORITES")
                    }
                })
            }
        })

        return binding.root
    }
    private fun showSnackBar(text: String) {
        Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }
}