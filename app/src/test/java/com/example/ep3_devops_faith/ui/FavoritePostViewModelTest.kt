package com.example.ep3_devops_faith.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.auth0.android.result.UserProfile
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.favorite.DatabaseFavorite
import com.example.ep3_devops_faith.database.favorite.FavoriteDatabaseDao
import com.example.ep3_devops_faith.database.post.DatabasePost
import com.example.ep3_devops_faith.database.post.PostDatabaseDao
import com.example.ep3_devops_faith.domain.Post
import com.example.ep3_devops_faith.getOrAwaitValue
import com.example.ep3_devops_faith.login.CredentialsManager
import com.example.ep3_devops_faith.ui.post.favorites.FavoritePostsOverviewViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class FavoritePostViewModelTest {
    private lateinit var favoriteDatabaseDao: FavoriteDatabaseDao
    private lateinit var postDatabaseDao: PostDatabaseDao
    private lateinit var database: FaithDatabase
    private val userX: String = "UserX"
    private val userEmail: String = "JohnDeere@gmail.com"
    private val testDispatcher = TestCoroutineDispatcher()
    private var userProfile: UserProfile = UserProfile(userX,
        userEmail,
        userEmail,
        null,
        userEmail,
        true,
        userEmail,
        null,
        null,
        null,
        null,
        null,
        null)

    @Before
    fun setup() {
        // Sets the given [dispatcher] as an underlying dispatcher of [Dispatchers.Main].
        // All consecutive usages of [Dispatchers.Main] will use given [dispatcher] under the hood.
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        // Resets state of the [Dispatchers.Main] to the original main dispatcher.
        // For example, in Android Main thread dispatcher will be set as [Dispatchers.Main].
        Dispatchers.resetMain()

        // Clean up the TestCoroutineDispatcher to make sure no other work is running.
        testDispatcher.cleanupTestCoroutines()
    }

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
        init()
    }

    private fun init() {
        createDatabasePost()
        createDatabaseFavoritesUserX()
        CredentialsManager.cachedUserProfile = userProfile
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    private fun createDatabasePost() {
        for (i in 1 until 6 step 1) {
            val databasePost = DatabasePost()
            databasePost.Text = "insertAndGetPost$i"
            databasePost.Link = "insertAndGetPost$i"
            databasePost.UserEmail = userEmail
            databasePost.UserId = userX
            postDatabaseDao.insert(databasePost)
            println("$databasePost")
        }
    }

    private fun createDatabaseFavoritesUserX() {
        for (i in 1 until 6 step 1) {
            val databaseFavorite = DatabaseFavorite()
            databaseFavorite.UserId = userX
            databaseFavorite.PostId = i.toLong()
            favoriteDatabaseDao.insert(databaseFavorite)
            println("$databaseFavorite")
        }
    }

    @Test
    fun isPostFavorite_test_false() = runBlocking {
        val favoritePostOverviewModel =
            FavoritePostsOverviewViewModel(database, ApplicationProvider.getApplicationContext())
        createDatabaseFavoritesUserX()
        val post = Post()
        post.Text = "post"
        post.Link = "www.google.com"
        post.UserId = userX
        post.UserEmail = userEmail
        val result = favoritePostOverviewModel.isFavorite(post).getOrAwaitValue()
        MatcherAssert.assertThat(result, Is.`is`(false))
    }
}