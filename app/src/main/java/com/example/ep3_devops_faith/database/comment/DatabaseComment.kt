package com.example.ep3_devops_faith.database.comment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ep3_devops_faith.domain.Comment

@Entity(tableName = "comment_table")
data class DatabaseComment(
    @PrimaryKey(autoGenerate = true)
    var Id: Long = 0L,
    var Message: String = "",
    @ColumnInfo(name = "user_id")
    val UserId: String = "",
    @ColumnInfo(name = "post_id")
    val PostId: Long = 0L,
    @ColumnInfo(name = "user_email")
    val UserEmail: String = ""
)
fun List<DatabaseComment>.asDomainmodel(): List<Comment> {
    return map {
        Comment(
            Id = it.Id,
            Message = it.Message,
            UserId = it.UserId,
            PostId = it.PostId,
            UserEmail = it.UserEmail
        )
    }
}