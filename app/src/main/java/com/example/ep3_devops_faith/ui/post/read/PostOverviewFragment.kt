package com.example.ep3_devops_faith.ui.post.read

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ep3_devops_faith.R
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.databinding.FragmentPostOverviewBinding

class PostOverviewFragment : Fragment() {
    lateinit var binding: FragmentPostOverviewBinding
    lateinit var viewModel: PostOverviewViewModel
    lateinit var adapter: PostAdapter
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

        // filling the list: post adapter
        adapter = PostAdapter(PostListener { postId ->
            Toast.makeText(context, "$postId", Toast.LENGTH_SHORT).show()
        })
        binding.postList.adapter = adapter

        // viewmodel
        val viewModelFactory = PostOverviewViewModelFactory(dataSource, application, adapter)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PostOverviewViewModel::class.java)

        // databinding
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // list changed
        viewModel.posts.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        return binding.root
    }
}