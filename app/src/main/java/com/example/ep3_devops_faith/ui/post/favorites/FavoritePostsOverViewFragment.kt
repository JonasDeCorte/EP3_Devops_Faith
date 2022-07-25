package com.example.ep3_devops_faith.ui.post.favorites

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
import com.example.ep3_devops_faith.databinding.FragmentFavoritePostsBinding
import com.example.ep3_devops_faith.ui.post.read.FavoriteListener
import com.example.ep3_devops_faith.ui.post.read.PostAdapter
import com.example.ep3_devops_faith.ui.post.read.PostListener
import com.example.ep3_devops_faith.ui.post.read.PostOverviewFragmentDirections
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class FavoritePostsOverViewFragment : Fragment() {
    lateinit var binding: FragmentFavoritePostsBinding
    private lateinit var favoritePostsOverviewViewModel: FavoritePostsOverviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_posts, container, false)

        // setup the db connection
        val application = requireNotNull(this.activity).application
        val dataSource = FaithDatabase.getInstance(application).postDatabaseDao
        // create the factory + viewmodel
        val viewModelFactory = FavoritePostsOverviewViewModelFactory(dataSource, application)
        favoritePostsOverviewViewModel =
            ViewModelProvider(this, viewModelFactory)[FavoritePostsOverviewViewModel::class.java]
        // Giving the binding access to the favoritePostsOverviewViewModel
        binding.favoritePostsOverviewViewModel = favoritePostsOverviewViewModel
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        // Sets the adapter of the PostAdapter RecyclerView with clickHandler lambda that
        // tells the viewModel when our property is clicked
        binding.postList.adapter = PostAdapter(PostListener {
            favoritePostsOverviewViewModel.displayPropertyDetails(it)
        }, FavoriteListener {
            favoritePostsOverviewViewModel.FavoriteClick(it)
        })

        // Observe the navigateToSelectedProperty LiveData and Navigate when it isn't null
        // After navigating, call displayPropertyDetailsComplete() so that the ViewModel is ready
        // for another navigation event.
        favoritePostsOverviewViewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, {
            if (null != it) {
                // Must find the NavController from the Fragment
                this.findNavController().navigate(
                    PostOverviewFragmentDirections.actionPostOverviewFragmentToPostDetailFragment(it)
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
                            Timber.i("REMOVED POST FROM FAVORITES")
                            favoritePostsOverviewViewModel.EventDone()
                        } else {
                            favoritePostsOverviewViewModel.saveFavorite(post)
                            favoritePostsOverviewViewModel.EventDone()
                            Timber.i(" SAVED POST TO FAVORITES")
                            showSnackBar("SAVED POST TO FAVORITES")
                        }
                    })
            }
        })
        favoritePostsOverviewViewModel.items.observe(viewLifecycleOwner, {
            Timber.i(" SOMETHING RANDOM")
            showSnackBar("NOT QUITE SURE")
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