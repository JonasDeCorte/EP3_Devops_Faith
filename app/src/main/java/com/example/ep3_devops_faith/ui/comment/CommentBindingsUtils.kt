package com.example.ep3_devops_faith.ui.comment

import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ep3_devops_faith.domain.Comment
import com.example.ep3_devops_faith.ui.comment.read.CommentAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import timber.log.Timber

@BindingAdapter("commentText")
fun TextView.setText(item: Comment) {
    item.let {
        text = item.Message
    }
}

@BindingAdapter("visibleButton")
fun Button.bindVisibility(visible: Boolean) {
    isVisible = visible == true
}

@BindingAdapter("visibleFloatingActionButton")
fun FloatingActionButton.bindVisibility(visible: Boolean) {
    isVisible = visible == true
}

@BindingAdapter("listDataComment")
fun bindRecyclerViewComment(recyclerView: RecyclerView, data: List<Comment>?) {
    if (data.isNullOrEmpty()) {
        Timber.i("LIST IS EMPTY")
        Timber.i(data.toString())
        return
    }
    Timber.i("LIST IS NOOOOOOT EMPTY")
    Timber.i(data.toString())
    val adapter = recyclerView.adapter as CommentAdapter
    adapter.submitList(data)
}

@BindingAdapter("userEmailText")
fun TextView.setCachedUserEmail(item: Comment) {
    item.let {
        text = item.UserEmail
    }
}