package com.example.ep3_devops_faith.ui.post.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
            Toast.makeText(requireContext(), "clicked", Toast.LENGTH_LONG).show()
        })
        commentViewModel.saveEvent.observe(viewLifecycleOwner, { saveEvent ->
            if (saveEvent) {
                Timber.i("REQUESTING TO SAVE A COMMENT")
                SaveComment(binding, post)
                commentViewModel.saveEventDone()
                ClearFields(binding)
                Timber.i("COMMENT HAS BEEN SAVED")
            }
        })
        return binding.root
    }

    private fun SaveComment(
        binding: FragmentPostDetailBinding,
        post: Post
    ) {
        commentViewModel.saveComment(
            binding.typeComment.text.toString(),
            post.Id,
            CredentialsManager.cachedUserProfile?.getId()!!
        )
    }

    private fun ClearFields(binding: FragmentPostDetailBinding) {
        binding.typeComment.setText("")
    }
}