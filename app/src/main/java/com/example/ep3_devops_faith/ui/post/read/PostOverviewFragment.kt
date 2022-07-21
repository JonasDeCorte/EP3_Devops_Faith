package com.example.ep3_devops_faith.ui.post.read

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ep3_devops_faith.R
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.databinding.FragmentPostOverviewBinding

class PostOverviewFragment : Fragment() {
    lateinit var binding: FragmentPostOverviewBinding
    lateinit var postViewModel: PostOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_post_overview, container, false)

        // setup the db connection
        val application = requireNotNull(this.activity).application
        val dataSource = FaithDatabase.getInstance(application).postDatabaseDao
        // create the factory + viewmodel
        val viewModelFactory = PostOverviewViewModelFactory(dataSource, application)
        postViewModel =
            ViewModelProvider(this, viewModelFactory).get(PostOverviewViewModel::class.java)
        // Giving the binding access to the PostOverviewViewModel
        binding.postViewModel = postViewModel
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        // Sets the adapter of the PostAdapter RecyclerView with clickHandler lambda that
        // tells the viewModel when our property is clicked
        binding.postList.adapter = PostAdapter(PostListener {
            postViewModel.displayPropertyDetails(it)
        })

        // Observe the navigateToSelectedProperty LiveData and Navigate when it isn't null
        // After navigating, call displayPropertyDetailsComplete() so that the ViewModel is ready
        // for another navigation event.
        postViewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                // Must find the NavController from the Fragment
                this.findNavController().navigate(
                    PostOverviewFragmentDirections.actionPostOverviewFragmentToPostDetailFragment(it)
                )
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                postViewModel.displayPropertyDetailsComplete()
            }
        })
        return binding.root
    }
}