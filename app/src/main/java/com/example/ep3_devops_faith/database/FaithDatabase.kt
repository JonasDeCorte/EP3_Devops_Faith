package com.example.ep3_devops_faith.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ep3_devops_faith.database.comment.CommentDatabaseDao
import com.example.ep3_devops_faith.database.comment.DatabaseComment
import com.example.ep3_devops_faith.database.post.DatabasePost
import com.example.ep3_devops_faith.database.post.PostDatabaseDao
import com.example.ep3_devops_faith.utils.ImageConverter

@TypeConverters(ImageConverter::class)
@Database(entities = [DatabasePost::class, DatabaseComment::class],
    version = 8,
    exportSchema = false)
abstract class FaithDatabase : RoomDatabase() {
    abstract val postDatabaseDao: PostDatabaseDao
    abstract val commentDatabaseDao: CommentDatabaseDao

    companion object {
        @Volatile // value always up to data & the same for all execution threads
        private var INSTANCE: FaithDatabase? = null

        fun getInstance(context: Context): FaithDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        FaithDatabase::class.java,
                        "faith_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}