package com.example.ep3_devops_faith.ui.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ep3_devops_faith.databinding.MovieListItemBinding
import com.example.ep3_devops_faith.network.MovieProperty
import timber.log.Timber

class MovieAdapter : ListAdapter<MovieProperty, ViewHolder>(PostDiffCallback()) {
    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<MovieProperty>() {
    override fun areItemsTheSame(oldItem: MovieProperty, newItem: MovieProperty): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: MovieProperty, newItem: MovieProperty): Boolean {
        return oldItem.imdbID == newItem.imdbID
    }
}

class ViewHolder(private var binding: MovieListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MovieProperty) {
        Timber.i(item.toString())
        binding.property = item
        binding.executePendingBindings()
    }

    // this way the viewHolder knows how to inflate.
    // better than having this in the adapter.
    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = MovieListItemBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }
    }
}
