package com.example.ep3_devops_faith.ui.comment.read

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ep3_devops_faith.databinding.CommentsBinding
import com.example.ep3_devops_faith.domain.Comment

class CommentAdapter(val clickListener: CommentListener) :
    ListAdapter<Comment, ViewHolder>(PostDiffCallback()) {
    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }
}

class ViewHolder(val binding: CommentsBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(clickListener: CommentListener, item: Comment) {
        binding.comment = item
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }

    // this way the viewHolder knows how to inflate.
    // better than having this in the adapter.
    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            println(layoutInflater.toString())
            val binding = CommentsBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.Id == newItem.Id
    }
}

/**
 * Custom listener that handles clicks on [RecyclerView] items.  Passes the [Comment]
 * associated with the current item to the [onClick] function.
 * @param clickListener lambda that will be called with the current [Comment]
 */
class CommentListener(val clickListener: (comment: Comment) -> Unit) {
    fun onClick(comment: Comment) = clickListener(comment)
}