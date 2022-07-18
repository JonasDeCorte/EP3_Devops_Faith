package com.example.ep3_devops_faith.ui.post.read

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.post.PostDatabaseDao
import com.example.ep3_devops_faith.repository.PostRepository

class PostOverviewViewModel(val database: PostDatabaseDao, app: Application) :
    AndroidViewModel(app) {
    val db = FaithDatabase.getInstance(app.applicationContext)
    val repository = PostRepository(db)
    val posts = repository.allPosts
}