package com.example.ep3_devops_faith.database.favorite

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ep3_devops_faith.domain.Favorite

@Entity(tableName = "favorite_table")
data class DatabaseFavorite(
    @PrimaryKey(autoGenerate = true)
    var Id: Long = 0L,
    @ColumnInfo(name = "user_id")
    var UserId: String = "",
    @ColumnInfo(name = "post_id")
    var PostId: Long = 0L
)

fun List<DatabaseFavorite>.asDomainmodel(): List<Favorite> {
    return map {
        Favorite(
            Id = it.Id,
            UserId = it.UserId,
            PostId = it.PostId
        )
    }
}