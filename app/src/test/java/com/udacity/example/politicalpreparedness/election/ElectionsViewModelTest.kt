package com.udacity.example.politicalpreparedness.election

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.udacity.example.politicalpreparedness.data.FakeTestRepository
import com.udacity.example.politicalpreparedness.MainCoroutineRule
import com.udacity.example.politicalpreparedness.election.model.ElectionModel
import com.udacity.example.politicalpreparedness.getOrAwaitValue
import com.udacity.example.politicalpreparedness.network.models.Division
import com.udacity.example.politicalpreparedness.network.models.Election
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsEmptyCollection
import org.hamcrest.core.IsInstanceOf
import org.hamcrest.core.IsNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import org.hamcrest.Matchers.`is` as matches

@ExperimentalCoroutinesApi
class ElectionsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var electionsRepository: FakeTestRepository
    private lateinit var electionsViewModel: ElectionsViewModel

    @Before
    fun setupViewModel() {
        electionsRepository = FakeTestRepository()
        val election1 = Election(1, "Title1", Date(), Division("1", "us", "al"))
        val election2 = Election(2, "Title2", Date(), Division("2", "us", "ga"))
        val election3 = Election(3, "Title3", Date(), Division("3", "us", "cl"))
        electionsRepository.addElections(election1, election2, election3)

        electionsViewModel = ElectionsViewModel(electionsRepository)
    }

    @Test
    fun refresh_ShowDataLoading() {
        mainCoroutineRule.pauseDispatcher()

        electionsViewModel.refresh()

        assertThat(electionsViewModel.dataLoading.getOrAwaitValue(), matches(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(electionsViewModel.dataLoading.getOrAwaitValue(), matches(false))
    }

    @Test
    fun refresh_LoadOnlyUpcomingElectionsExists() {
        electionsViewModel.refresh()

        assertThat(electionsViewModel.upcomingElections.getOrAwaitValue().isEmpty(), matches(false))

        assertThat(electionsViewModel.upcomingElections.getOrAwaitValue().size, matches(3))
        assertThat(electionsViewModel.savedElections.getOrAwaitValue(), matches(IsEmptyCollection()))
    }

    @Test
    fun refresh_LoadUpcomingAndSavedElections() {
        electionsRepository.addElections(Election(1, "Title1", Date(), Division("1", "us", "al")).copy(saved = true))
        electionsViewModel.refresh()

        assertThat(electionsViewModel.upcomingElections.getOrAwaitValue().isEmpty(), matches(false))

        assertThat(electionsViewModel.upcomingElections.getOrAwaitValue().size, matches(3))
        assertThat(electionsViewModel.savedElections.getOrAwaitValue().size, matches(1))
    }

    @Test
    fun upcomingElectionClicked() {
        electionsViewModel.onUpcomingClicked(ElectionModel(1, "Title1", Date(), Division("1", "us", "al"), false))

        assertThat(electionsViewModel.navigateTo.getOrAwaitValue(), IsInstanceOf(ElectionsFragmentDirections.ActionElectionsFragmentToVoterInfoFragment::class.java))
    }

    @Test
    fun savedElectionClicked() {
        electionsViewModel.onSavedClicked(ElectionModel(1, "Title1", Date(), Division("1", "us", "al"), false))

        assertThat(electionsViewModel.navigateTo.getOrAwaitValue(), IsInstanceOf(ElectionsFragmentDirections.ActionElectionsFragmentToVoterInfoFragment::class.java))
    }

    @Test
    fun navigationCompleted_ClearNavigateTo() {
        electionsViewModel.navigateCompleted()

        assertThat(electionsViewModel.navigateTo.getOrAwaitValue(), IsNull())
    }
}