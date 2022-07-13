package com.example.ep3_devops_faith

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ep3_devops_faith.database.FaithDatabase
import com.example.ep3_devops_faith.database.user.DatabaseUser
import com.example.ep3_devops_faith.database.user.UserDatabaseDao
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class FaithDatabaseTest {
    private lateinit var userDatabaseDao: UserDatabaseDao
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
        userDatabaseDao = database.userDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetUser() {
        val databaseUser = DatabaseUser()
        databaseUser.Auth0Key = "insertAndGetUser"
        userDatabaseDao.insert(databaseUser)
        val user = userDatabaseDao.getByKey("insertAndGetUser")
        assertEquals(user.Auth0Key, "insertAndGetUser")
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetUser() {
        val databaseUser = DatabaseUser()
        databaseUser.Auth0Key = "insertAndGetUser"
        userDatabaseDao.insert(databaseUser)
        val user = userDatabaseDao.getByKey("insertAndGetUser")
        assertEquals(user.Auth0Key, "insertAndGetUser")
        user.Auth0Key = "editAuth0Key"
        userDatabaseDao.update(user)
        assertEquals(user.Auth0Key, "editAuth0Key")
    }
}