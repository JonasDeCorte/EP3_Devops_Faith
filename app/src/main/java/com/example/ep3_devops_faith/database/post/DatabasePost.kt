package com.example.ep3_devops_faith.database.post

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ep3_devops_faith.domain.Post

@Entity(tableName = "post_table")
data class DatabasePost(
    @PrimaryKey(autoGenerate = true)
    var Id: Long = 0L,
    var Text: String = "",
    @ColumnInfo(name = "image", typeAffinity = ColumnInfo.BLOB)
    var Picture: Bitmap? = null,
    var Links: List<String>? = null,
    @ColumnInfo(name = "person_id")
    var PersonId: Long = 0L
)

fun List<DatabasePost>.asDomainmodel(): List<Post> {
    return map {
        Post(
            Id = it.Id,
            Text = it.Text,
            Picture = it.Picture,
            Links = it.Links,
            PersonId = it.PersonId
        )
    }
}