package com.example.ep3_devops_faith.ui.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ep3_devops_faith.R
import com.example.ep3_devops_faith.databinding.MovieListItemBinding
import com.example.ep3_devops_faith.domain.Movie

class MovieAdapter : RecyclerView.Adapter<MovieViewHolder>() {

    private var movies = mutableListOf<Movie>()

    fun setMovieList(movies: List<Movie>) {
        this.movies = movies.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = MovieListItemBinding.inflate(inflater, parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.binding.movieTitle.text = movie.title
        Glide.with(holder.itemView.context).load(movie.poster)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.binding.moviePoster)
    }

    override fun getItemCount(): Int {
        return movies.size
    }
}

class MovieViewHolder(val binding: MovieListItemBinding) : RecyclerView.ViewHolder(binding.root)