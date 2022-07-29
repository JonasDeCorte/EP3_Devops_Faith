package com.example.ep3_devops_faith.ui.post.detail

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.ep3_devops_faith.R
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.databinding.FragmentPostDetailBinding
import com.example.ep3_devops_faith.domain.Post
import com.example.ep3_devops_faith.login.CredentialsManager
import com.example.ep3_devops_faith.ui.comment.read.CommentAdapter
import com.example.ep3_devops_faith.ui.comment.read.CommentListener
import com.example.ep3_devops_faith.ui.comment.read.CommentViewModel
import com.example.ep3_devops_faith.ui.comment.read.CommentViewModelFactory
import timber.log.Timber

class PostDetailFragment : Fragment() {
    lateinit var commentViewModel: CommentViewModel
    lateinit var postViewModel: PostDetailViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(activity).application
        val dataSource = FaithDatabase.getInstance(application).commentDatabaseDao
        val binding = FragmentPostDetailBinding.inflate(inflater)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        val post = PostDetailFragmentArgs.fromBundle(requireArguments()).selectedProperty
        val postViewModelFactory = PostDetailViewModelFactory(post, application)
        postViewModel =
            ViewModelProvider(this, postViewModelFactory).get(PostDetailViewModel::class.java)
        // Giving the binding access to the postViewModel
        binding.postViewModel = postViewModel
        val commentViewModelFactory = CommentViewModelFactory(post, dataSource, application)
        commentViewModel =
            ViewModelProvider(this, commentViewModelFactory).get(CommentViewModel::class.java)
        // Giving the binding access to the commentViewModel
        binding.commentViewModel = commentViewModel
        // Sets the adapter of the commentList RecyclerView with clickHandler lambda that
        // tells the viewModel when our property is clicked
        binding.commentList.adapter = CommentAdapter(CommentListener {
            commentViewModel.displayPropertyDetails(it)
        })
        commentViewModel.saveEvent.observe(viewLifecycleOwner, { saveEvent ->
            if (saveEvent) {
                Timber.i("REQUESTING TO SAVE A COMMENT")
                SaveComment(binding, post)
                commentViewModel.saveEventDone()
                ClearFields(binding)
                requireView().hideSoftInput()
                Timber.i("COMMENT HAS BEEN SAVED")
            }
        })

        // Observe the navigateToSelectedProperty LiveData and Navigate when it isn't null
        // After navigating, call displayPropertyDetailsComplete() so that the ViewModel is ready
        // for another navigation event.
        commentViewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                // Must find the NavController from the Fragment
                this.findNavController().navigate(
                    PostDetailFragmentDirections.actionPostDetailFragmentToCommentDetailFragment(it)
                )
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                commentViewModel.displayPropertyDetailsComplete()
            }
        })
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun SaveComment(
        binding: FragmentPostDetailBinding,
        post: Post
    ) {
        commentViewModel.saveComment(
            binding.typeComment.text.toString(),
            post.Id,
            CredentialsManager.cachedUserProfile?.getId()!!,
            CredentialsManager.cachedUserProfile?.email.toString()
        )
    }

    private fun ClearFields(binding: FragmentPostDetailBinding) {
        binding.typeComment.setText("")
    }

    fun View.hideSoftInput() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
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