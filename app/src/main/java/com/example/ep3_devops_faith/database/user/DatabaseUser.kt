package com.example.ep3_devops_faith.database.user

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ep3_devops_faith.domain.User

@Entity(tableName = "user_table")
data class DatabaseUser(
    @PrimaryKey(autoGenerate = true)
    var Id: Long = 0L,
    @ColumnInfo(name = "auth0_key")
    var Auth0Key: String = "",
    var Name: String = "",
    @ColumnInfo(name = "image", typeAffinity = ColumnInfo.BLOB)
    var ProfileImageUrl: Bitmap? = null,
    var Role: String = ""
)

fun List<DatabaseUser>.asDomainmodel(): List<User> {
        return map {
                User(
                    Id = it.Id,
                    Name = it.Name,
                    Auth0_key = it.Auth0Key,
                    ProfileImageUrl = it.ProfileImageUrl,
                    Role = it.Role
                )
        }
}