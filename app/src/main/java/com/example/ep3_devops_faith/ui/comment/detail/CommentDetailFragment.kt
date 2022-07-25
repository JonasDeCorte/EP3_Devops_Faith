package com.example.ep3_devops_faith.ui.comment.detail
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ep3_devops_faith.databinding.FragmentCommentDetailBinding
import com.example.ep3_devops_faith.login.CredentialsManager
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class CommentDetailFragment : Fragment() {
    lateinit var commentViewModel: CommentDetailViewModel
    private lateinit var binding: FragmentCommentDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(activity).application
        binding = FragmentCommentDetailBinding.inflate(inflater)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        val comment = CommentDetailFragmentArgs.fromBundle(requireArguments()).selectedProperty
        val commentDetailViewModelFactory = CommentDetailViewModelFactory(comment, application)
        commentViewModel =
            ViewModelProvider(this,
                commentDetailViewModelFactory).get(CommentDetailViewModel::class.java)
        // Giving the binding access to the commentViewModel
        binding.commentViewModel = commentViewModel
        commentViewModel.updateEvent.observe(viewLifecycleOwner, { updateEvent ->
            if (updateEvent) {
                if (CredentialsManager.cachedUserProfile!!.getId().equals(comment.UserId)) {
                    Timber.i("REQUESTING TO UPDATE A COMMENT")
                    comment.Message = binding.commenttext.text.toString()
                    commentViewModel.updateComment(comment)
                    this.findNavController().navigate(
                        CommentDetailFragmentDirections.actionCommentDetailFragmentToPostOverviewFragment()
                    )
                    requireView().hideSoftInput()
                    commentViewModel.updateEventDone()
                    Timber.i("COMMENT HAS BEEN UPDATED")
                } else {
                    showSnackBar("CANNOT UPDATE COMMENT - CANNOT  UPDATE FOREIGN COMMENTS")
                }
            }
        })
        commentViewModel.deleteEvent.observe(viewLifecycleOwner, { deleteEvent ->
            if (deleteEvent) {
                if (CredentialsManager.cachedUserProfile!!.getId().equals(comment.UserId)) {
                    Timber.i("REQUESTING TO DELETE A COMMENT")
                    commentViewModel.deleteComment(comment)
                    this.findNavController().navigate(
                        CommentDetailFragmentDirections.actionCommentDetailFragmentToPostOverviewFragment()
                    )
                    commentViewModel.deleteEventDone()
                    Timber.i("COMMENT HAS BEEN DELETED")
                } else {
                    showSnackBar("CANNOT DELETE COMMENT - CANNOT DELETE FOREIGN COMMENTS")
                }
            }
        })
        return binding.root
    }

    fun View.hideSoftInput() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }
}