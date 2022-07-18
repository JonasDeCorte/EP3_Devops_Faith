package com.example.ep3_devops_faith.ui.post

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.ep3_devops_faith.domain.Post

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
