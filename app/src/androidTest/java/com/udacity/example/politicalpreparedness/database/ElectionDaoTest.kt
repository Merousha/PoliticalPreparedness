package com.udacity.example.politicalpreparedness.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.example.politicalpreparedness.network.models.Division
import com.udacity.example.politicalpreparedness.network.models.Election
import com.udacity.example.politicalpreparedness.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNull
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import org.hamcrest.Matchers.`is` as matches

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ElectionDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ElectionDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                ElectionDatabase::class.java
        ).build()

    }

    @After
    fun tearDown() = database.close()

    @Test
    fun addElection() = runBlockingTest {
        val election1 = Election(1, "Title1", Date(), Division("1", "us", "al"))

        database.electionDao.save(election1)

        val result = database.electionDao.get(1, "1")

        assertThat(result as Election, IsNull.notNullValue())
        assertThat(result.id, matches(result.id))
        assertThat(result.name, matches(result.name))
        assertThat(result.saved, matches(result.saved))
        assertThat(result.division.id, matches(result.division.id))
        assertThat(result.division.country, matches(result.division.country))
        assertThat(result.division.state, matches(result.division.state))
    }

    @Test
    fun getAllSaved() = runBlockingTest {
        val election1 = Election(1, "Title1", Date(), Division("1", "us", "al"))
        val election2 = Election(2, "Title1", Date(), Division("1", "us", "al"))
        val election3 = Election(3, "Title1", Date(), Division("1", "us", "al"))

        database.electionDao.saveAll(listOf(election1, election2, election3))

        val result = database.electionDao.getAll().getOrAwaitValue()

        assertThat(result.size, matches(3))
    }

    @Test
    fun markElectionAsSaved() = runBlockingTest {
        val election1 = Election(1, "Title1", Date(), Division("1", "us", "al"))

        database.electionDao.save(election1)

        val saved = database.electionDao.get(1, "1")

        assertThat(saved as Election, IsNull.notNullValue())
        assertThat(saved.saved, matches(false))

        database.electionDao.markAsSaved(1, "1")

        val updatedSaved = database.electionDao.get(1, "1")
        assertThat(updatedSaved as Election, IsNull.notNullValue())
        assertThat(updatedSaved.saved, matches(true))
    }

    @Test
    fun markElectionAsNotSaved() = runBlockingTest {
        val election1 = Election(1, "Title1", Date(), Division("1", "us", "al"), saved = true)

        database.electionDao.save(election1)

        val saved = database.electionDao.get(1, "1")

        assertThat(saved as Election, IsNull.notNullValue())
        assertThat(saved.saved, matches(true))

        database.electionDao.markAsNotSaved(1, "1")

        val updatedSaved = database.electionDao.get(1, "1")
        assertThat(updatedSaved as Election, IsNull.notNullValue())
        assertThat(updatedSaved.saved, matches(false))
    }

    @Test
    fun deleteAllElections() = runBlockingTest {
        val election1 = Election(1, "Title1", Date(), Division("1", "us", "al"))
        val election2 = Election(2, "Title1", Date(), Division("1", "us", "al"))
        val election3 = Election(3, "Title1", Date(), Division("1", "us", "al"))

        database.electionDao.saveAll(listOf(election1, election2, election3))

        val result = database.electionDao.getAllSync()

        assertThat(result.size, matches(3))

        database.electionDao.deleteAll()

        val updatedResult = database.electionDao.getAll().getOrAwaitValue()

        assertThat(updatedResult.size, matches(0))
    }
}