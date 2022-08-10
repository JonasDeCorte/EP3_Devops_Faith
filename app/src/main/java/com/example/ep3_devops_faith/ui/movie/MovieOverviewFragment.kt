package com.example.ep3_devops_faith.ui.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ep3_devops_faith.databinding.FragmentMovieOverviewBinding
import com.example.ep3_devops_faith.network.RetrofitService
import com.example.ep3_devops_faith.repository.MovieRepository
import timber.log.Timber

class MovieOverviewFragment : Fragment() {
    private lateinit var binding: FragmentMovieOverviewBinding
    private lateinit var viewModel: MovieViewModel
    private val retrofitService = RetrofitService.getInstance()
    val adapter = MovieAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMovieOverviewBinding.inflate(inflater)
        // get viewmodel instance using MovieViewModelFactory
        viewModel =
            ViewModelProvider(this, MovieViewModelFactory(MovieRepository(retrofitService))).get(
                MovieViewModel::class.java
            )
        // set recyclerview adapter
        binding.recyclerview.adapter = adapter

        viewModel.movieList.observe(viewLifecycleOwner, Observer {
            Timber.i("movieList: $it")
            adapter.setMovieList(it)
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Timber.d("movieList: $it")
        })

        viewModel.getAllMovies()
        return binding.root
    }
}