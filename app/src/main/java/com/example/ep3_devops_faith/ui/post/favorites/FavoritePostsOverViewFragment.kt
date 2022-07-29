package com.example.ep3_devops_faith.ui.post.favorites

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.ep3_devops_faith.R
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.databinding.FragmentFavoritePostsBinding
import com.example.ep3_devops_faith.login.CredentialsManager
import com.example.ep3_devops_faith.ui.post.read.FavoriteListener
import com.example.ep3_devops_faith.ui.post.read.PostAdapter
import com.example.ep3_devops_faith.ui.post.read.PostListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoritePostsOverViewFragment : Fragment() {
    lateinit var binding: FragmentFavoritePostsBinding
    private lateinit var favoritePostsOverviewViewModel: FavoritePostsOverviewViewModel
    private val role =
        CredentialsManager.cachedUserProfile!!.getUserMetadata().get("Role") as String?

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // setup the db connection
        val application = requireNotNull(this.activity).application
        val dataSource = FaithDatabase.getInstance(application)
        // create the factory + viewmodel
        val viewModelFactory = FavoritePostsOverviewViewModelFactory(dataSource, application)
        favoritePostsOverviewViewModel =
            ViewModelProvider(this, viewModelFactory)[FavoritePostsOverviewViewModel::class.java]
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_posts, container, false)
        // Giving the binding access to the favoritePostsOverviewViewModel
        binding.favoritePostsOverviewViewModel = favoritePostsOverviewViewModel
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        // Sets the adapter of the PostAdapter RecyclerView with clickHandler lambda that
        // tells the viewModel when our property is clicked
        binding.postList.adapter = PostAdapter(PostListener {
            favoritePostsOverviewViewModel.displayPropertyDetails(it)
        }, FavoriteListener {
            favoritePostsOverviewViewModel.favoriteClick(it)
        })
        // Observe the navigateToSelectedProperty LiveData and Navigate when it isn't null
        // After navigating, call displayPropertyDetailsComplete() so that the ViewModel is ready
        // for another navigation event.
        favoritePostsOverviewViewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, {
            if (null != it) {
                // Must find the NavController from the Fragment
                this.findNavController().navigate(
                    FavoritePostsOverViewFragmentDirections.actionFavoritePostsOverViewFragmentToPostDetailFragment(
                        it)
                )
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                favoritePostsOverviewViewModel.displayPropertyDetailsComplete()
            }
        })
        favoritePostsOverviewViewModel.event.observe(viewLifecycleOwner, { post ->
            if (null != post) {
                favoritePostsOverviewViewModel.isFavorite(post)
                    .observe(viewLifecycleOwner, { isFav ->
                        if (isFav) {
                            favoritePostsOverviewViewModel.removeFavorite(post)
                            showSnackBar("REMOVED POST FROM FAVORITES")
                            Timber.i("REMOVED POST FROM FAVORITES $post")
                            favoritePostsOverviewViewModel.eventDone()
                        } else {
                            favoritePostsOverviewViewModel.saveFavorite(post)
                            showSnackBar("SAVED POST TO FAVORITES")
                            Timber.i(" SAVED POST TO FAVORITES $post")
                            favoritePostsOverviewViewModel.eventDone()
                        }
                    })
            }
        })
        viewLifecycleOwner.lifecycleScope.launch {
            favoritePostsOverviewViewModel.items.observe(viewLifecycleOwner, { list ->
                showSnackBar("items")
            })
        }
        setHasOptionsMenu(true)
        return binding.root
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