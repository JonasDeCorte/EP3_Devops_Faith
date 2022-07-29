package com.example.ep3_devops_faith

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.comment.CommentDatabaseDao
import com.example.ep3_devops_faith.database.comment.DatabaseComment
import com.example.ep3_devops_faith.database.favorite.DatabaseFavorite
import com.example.ep3_devops_faith.database.favorite.FavoriteDatabaseDao
import com.example.ep3_devops_faith.database.post.DatabasePost
import com.example.ep3_devops_faith.database.post.PostDatabaseDao
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class FaithDatabaseTest {
    private lateinit var favoriteDatabaseDao: FavoriteDatabaseDao
    private lateinit var postDatabaseDao: PostDatabaseDao
    private lateinit var commentDatabaseDao: CommentDatabaseDao

    private lateinit var database: FaithDatabase
    private val userX: String = "UserX"
    private val UserEmail: String = "JohnDeere@gmail.com"

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // using an in-memory database because the information stored here disappears when the process is killed.
        database = Room.inMemoryDatabaseBuilder(
            context,
            FaithDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        favoriteDatabaseDao = database.favoriteDatabaseDao
        postDatabaseDao = database.postDatabaseDao
        commentDatabaseDao = database.commentDatabaseDao
    }

    private fun createDatabasePost() {
        for (i in 1 until 6 step 1) {
            val databasePost = DatabasePost()
            databasePost.Text = "insertAndGetPost$i"
            databasePost.Link = "insertAndGetPost$i"
            postDatabaseDao.insert(databasePost)
            println("$databasePost")
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    private fun createDatabaseFavoritesUserX() {
        for (i in 1 until 6 step 1) {
            var databaseFavorite = DatabaseFavorite()
            databaseFavorite.UserId = userX
            databaseFavorite.PostId = i.toLong()
            favoriteDatabaseDao.insert(databaseFavorite)
            println("$databaseFavorite")
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetFavoriteForUserX() {
        createDatabasePost()
        assertEquals(5, postDatabaseDao.getDataCount())
        postDatabaseDao.getAllEntries()
        createDatabaseFavoritesUserX()
        assertEquals(5, favoriteDatabaseDao.getDataCountFavorites(userX).size)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetPost() {
        createDatabasePost()
        assertEquals(5, postDatabaseDao.getDataCount())
    }

    @Test
    @Throws(Exception::class)
    fun deletePost() {
        createDatabasePost()
        assertEquals(5, postDatabaseDao.getDataCount())
        postDatabaseDao.delete(postDatabaseDao.get(1.toLong()))
        assertEquals(4, postDatabaseDao.getDataCount())
    }

    @Test
    @Throws(Exception::class)
    fun updatePost() {
        createDatabasePost()
        assertEquals(5, postDatabaseDao.getDataCount())
        val post = postDatabaseDao.get(1.toLong())
        post.Text = "updated db post"
        postDatabaseDao.update(post)
        assertEquals("updated db post", postDatabaseDao.get(1.toLong()).Text)
    }

    @Test
    @Throws(Exception::class)
    fun addCommentForUserX() {
        val comment = DatabaseComment(
            Message = "comment",
            UserEmail = UserEmail,
            UserId = userX,
            PostId = 1.toLong()
        )
        commentDatabaseDao.insert(comment)
        val count = commentDatabaseDao.getDataCount()
        assertEquals(1, count)
    }

    fun deleteCommentForUser() {
        val comment = DatabaseComment(
            Message = "comment",
            UserEmail = UserEmail,
            UserId = userX,
            PostId = 1.toLong()
        )
        commentDatabaseDao.insert(comment)
        var count = commentDatabaseDao.getDataCount()
        assertEquals(1, count)
        commentDatabaseDao.delete(comment)
        count = commentDatabaseDao.getDataCount()
        assertEquals(0, count)
    }

    @Test
    @Throws(Exception::class)
    fun removefavoriteForUserX() {
        createDatabasePost()
        createDatabaseFavoritesUserX()
        val dbfav = favoriteDatabaseDao.get(1.toLong(), userX)
        favoriteDatabaseDao.delete(dbfav)
        assertEquals(4, favoriteDatabaseDao.getDataCountFavorites(userX).size)
    }

    @Test
    @Throws(Exception::class)
    fun totalAmountOfFavorites() {
        createDatabasePost()
        createDatabaseFavoritesUserX()
        val count = favoriteDatabaseDao.getDataCount()
        assertEquals(5, count)
    }
}