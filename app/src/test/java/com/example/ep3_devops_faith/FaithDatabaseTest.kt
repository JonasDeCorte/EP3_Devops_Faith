package com.example.ep3_devops_faith

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ep3_devops_faith.database.FaithDatabase
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
    private lateinit var database: FaithDatabase

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
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetFavorite() {
        for (i in 1 until 6 step 1) {
            val databasePost = DatabasePost()
            databasePost.Text = "insertAndGetPost$i"
            databasePost.Link = "insertAndGetPost$i"
            postDatabaseDao.insert(databasePost)
            println("$databasePost")
        }
        assertEquals(5, postDatabaseDao.getDataCount())
        postDatabaseDao.getAllEntries()
        var databaseFavorite = DatabaseFavorite()
        databaseFavorite.UserId = "userId"
        databaseFavorite.PostId = 1.toLong()
        favoriteDatabaseDao.insert(databaseFavorite)
        println("eerste db fav $databaseFavorite")
        databaseFavorite.UserId = "user"
        databaseFavorite.PostId = 1.toLong()
        favoriteDatabaseDao.insert(databaseFavorite)
        println("tweede db fav $databaseFavorite")
        var list = favoriteDatabaseDao.getDataCountFavorites("userId")
        assertEquals(1, list.size)
        assertEquals(2, favoriteDatabaseDao.getDataCount())
        databaseFavorite.UserId = "gebruiker"
        databaseFavorite.PostId = 1.toLong()
        favoriteDatabaseDao.insert(databaseFavorite)
        println("derde db fav $databaseFavorite")
        assertEquals(3, favoriteDatabaseDao.getDataCount())
        databaseFavorite.UserId = "userId"
        databaseFavorite.PostId = 2.toLong()
        favoriteDatabaseDao.insert(databaseFavorite)
        println("vierde db fav $databaseFavorite")
        list = favoriteDatabaseDao.getDataCountFavorites("userId")
        println("list=  $list")
        assertEquals(2, list.size)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetPost() {
        for (i in 1 until 6 step 1) {
            val databasePost = DatabasePost()
            databasePost.Text = "insertAndGetPost$i"
            databasePost.Link = "insertAndGetPost$i"
            postDatabaseDao.insert(databasePost)
            println("$databasePost")
        }
        assertEquals(5, postDatabaseDao.getDataCount())
    }
}