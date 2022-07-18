package com.example.ep3_devops_faith.ui.post.read

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ep3_devops_faith.databinding.PostListItemBinding
import com.example.ep3_devops_faith.domain.Post

class PostAdapter(val clickListener: PostListener) :
    ListAdapter<Post, ViewHolder>(PostDiffCallback()) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }
}

class ViewHolder(val binding: PostListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(clickListener: PostListener, item: Post) {
        binding.post = item
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }

    // this way the viewHolder knows how to inflate.
    // better than having this in the adapter.
    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            println(layoutInflater.toString())
            val binding = PostListItemBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.Id == newItem.Id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

class PostListener(val clickListener: (postId: Long) -> Unit) {
    fun onClick(post: Post) = clickListener(post.Id)
}