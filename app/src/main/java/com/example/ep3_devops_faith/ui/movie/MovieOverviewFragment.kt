package com.example.ep3_devops_faith.ui.movie

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.ep3_devops_faith.R
import com.example.ep3_devops_faith.databinding.FragmentMovieOverviewBinding
import com.example.ep3_devops_faith.login.CredentialsManager

class MovieOverviewFragment : Fragment() {
    /**
     * Lazily initialize our [OverviewViewModel].
     */
    private val viewModel: MovieViewModel by lazy {
        ViewModelProvider(this).get(MovieViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = FragmentMovieOverviewBinding.inflate(inflater)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.setLifecycleOwner(this)
        // Giving the binding access to the MovieViewModel
        binding.viewModel = viewModel
        // Sets the adapter of the photosGrid RecyclerView
        binding.moviesOverview.adapter = MovieAdapter()
        setHasOptionsMenu(true)
        return binding.root
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