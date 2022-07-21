package com.example.ep3_devops_faith.ui.comment

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ep3_devops_faith.domain.Comment
import com.example.ep3_devops_faith.login.CredentialsManager
import com.example.ep3_devops_faith.ui.comment.read.CommentAdapter

@BindingAdapter("userImage")
fun ImageView.setUserProfileImage(item: Comment) {
    setImageURI(Uri.parse(CredentialsManager.cachedUserProfile!!.pictureURL))
}

@BindingAdapter("commentText")
fun TextView.setText(item: Comment) {
    item.let {
        text = item.Message
    }
}

@BindingAdapter("listDataComment")
fun bindRecyclerViewComment(recyclerView: RecyclerView, data: List<Comment>?) {
    if (data.isNullOrEmpty()) {
        return
    }
    val adapter = recyclerView.adapter as CommentAdapter
    adapter.submitList(data)
}

@BindingAdapter("userEmailText")
fun TextView.setCachedUserEmail(item: Comment) {
    item.let {
        text = CredentialsManager.cachedUserProfile!!.email
    }
}