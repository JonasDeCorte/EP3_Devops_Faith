package com.example.ep3_devops_faith.ui.post

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ep3_devops_faith.R
import com.example.ep3_devops_faith.domain.Post
import com.example.ep3_devops_faith.login.CredentialsManager
import com.example.ep3_devops_faith.ui.post.read.PostAdapter
import timber.log.Timber

@BindingAdapter("postImage")
fun ImageView.setPostImage(item: Post) {
    setImageBitmap(item.Picture)
}

@BindingAdapter("postText")
fun TextView.setText(item: Post) {
    item.let {
        text = item.Text
    }
}

@BindingAdapter("postLinkText")
fun TextView.setLink(item: Post) {
    item.let {
        text = item.Link
    }
}

@BindingAdapter("isFavJongere")
fun ImageView.isVisible(item: Post) {
    item.let {
        val role = CredentialsManager.cachedUserProfile!!.getUserMetadata().get("Role") as String?
        Timber.i("Role:= " + role.toString())
        if (role.equals(context.getString(R.string.Type_Jongere))) {
            visibility = ImageView.VISIBLE
        } else {
            visibility = ImageView.GONE
        }
    }
}

@BindingAdapter("postStatus")
fun Button.setStatus(item: Post) {
    Timber.i(item.toString())
    item.let {
        val role = CredentialsManager.cachedUserProfile!!.getUserMetadata().get("Role") as String?
        Timber.i("Role:= " + role.toString())
        if (role.equals(context.getString(R.string.Type_Jongere))) {
            text = ""
            visibility = TextView.GONE
        } else {
            Timber.i("item.status ===== ${item.Status}")
            when (item.Status) {
                "NEW" -> {
                    Timber.i("NEW")
                    visibility = View.VISIBLE
                    text = context.getString(R.string.Enum_New)
                }
                "READ" -> {
                    Timber.i("READ")
                    visibility = View.VISIBLE
                    text = context.getString(R.string.Enum_Read)
                }
                "ANSWERED" -> {
                    Timber.i("ANSWERED")
                    visibility = View.VISIBLE
                    text = context.getString(R.string.Enum_Answered)
                }
            }
        }
    }
}

@BindingAdapter("listData")
fun bindRecyclerViewPost(recyclerView: RecyclerView, data: List<Post>?) {
    if (data.isNullOrEmpty()) {
        Timber.i("LIST IS EMPTY")
        Timber.i("bindRecyclerViewPost=== $data")
        return
    }
    Timber.i("LIST IS NOOOOOOT EMPTY")
    Timber.i(" bindRecyclerViewPost ==== $data")
    val adapter = recyclerView.adapter as PostAdapter
    adapter.submitList(data)
}

@BindingAdapter("postUser")
fun TextView.setUserEmail(item: Post) {
    item.let {
        text = item.UserEmail
    }
}
