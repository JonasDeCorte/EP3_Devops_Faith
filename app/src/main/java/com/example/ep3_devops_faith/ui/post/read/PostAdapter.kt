package com.example.ep3_devops_faith.ui.post.read

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ep3_devops_faith.databinding.PostListItemBinding
import com.example.ep3_devops_faith.domain.Post

class PostAdapter(val clickListener: PostListener, val favoriteListener: FavoriteListener) :
    ListAdapter<Post, ViewHolder>(PostDiffCallback()) {

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, favoriteListener, item)
    }
    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }
}

class ViewHolder(val binding: PostListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(clickListener: PostListener, favoriteListener: FavoriteListener, item: Post) {
        binding.post = item
        binding.clickListener = clickListener
        binding.favoriteListener = favoriteListener
        binding.executePendingBindings()
    }

    // this way the viewHolder knows how to inflate.
    // better than having this in the adapter.
    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = PostListItemBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.Id == newItem.Id
    }
}

/**
 * Custom listener that handles clicks on [RecyclerView] items.  Passes the [Post]
 * associated with the current item to the [onClick] function.
 * @param clickListener lambda that will be called with the current [Post]
 */
class PostListener(val clickListener: (post: Post) -> Unit) {
    fun onClick(post: Post) = clickListener(post)
}
class FavoriteListener(val clickListener: (post: Post) -> Unit) {
    fun onClick(post: Post) = clickListener(post)
}
