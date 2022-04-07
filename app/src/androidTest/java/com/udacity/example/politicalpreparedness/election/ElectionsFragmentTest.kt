package com.udacity.example.politicalpreparedness.election

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.example.politicalpreparedness.BaseFragmentTest
import com.udacity.example.politicalpreparedness.R
import com.udacity.example.politicalpreparedness.election.adapter.ElectionListAdapter
import com.udacity.example.politicalpreparedness.election.model.ElectionModel
import com.udacity.example.politicalpreparedness.network.models.Division
import com.udacity.example.politicalpreparedness.network.models.Election
import com.udacity.example.politicalpreparedness.util.RecyclerViewItemCountAssertion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.not
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import java.util.*

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class ElectionsFragmentTest : BaseFragmentTest() {

    @Test
    fun displayOnlyUpcomingElections() = runBlockingTest {
        val election1 = Election(1, "Title1", Date(), Division("1", "us", "al"))
        val election2 = Election(2, "Title2", Date(), Division("2", "us", "ga"))
        val election3 = Election(3, "Title3", Date(), Division("3", "us", "cl"))
        repository.addElections(election1, election2, election3)

        val scenario = launchFragmentInContainer<ElectionsFragment>(null, R.style.AppTheme)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.upcoming_elections_recycler)).check(matches(isDisplayed()))

        onView(withId(R.id.upcoming_elections_recycler))
                .check(RecyclerViewItemCountAssertion.withItemCount(3))
        onView(withId(R.id.upcoming_elections_header)).check(matches(isDisplayed()))
        onView(withId(R.id.upcoming_elections_header)).check(matches(withText("Upcoming elections")))
        onView(withId(R.id.saved_elections_header)).check(matches(not(isDisplayed())))
        onView(withId(R.id.saved_elections_recycler)).check(matches(isDisplayed()))
        onView(withId(R.id.saved_elections_recycler))
                .check(RecyclerViewItemCountAssertion.withItemCount(0))
    }

    @Test
    fun displayUpcomingAndSavedElections() = runBlockingTest {
        val election1 = Election(1, "Title1", Date(), Division("1", "us", "al"))
        val election2 = Election(2, "Title2", Date(), Division("2", "us", "ga"))
        val election3 = Election(3, "Title3", Date(), Division("3", "us", "cl"), saved = true)
        repository.addElections(election1, election2, election3)

        val scenario = launchFragmentInContainer<ElectionsFragment>(null, R.style.AppTheme)

        onView(withId(R.id.upcoming_elections_recycler)).check(matches(isDisplayed()))

        onView(withId(R.id.upcoming_elections_recycler))
                .check(RecyclerViewItemCountAssertion.withItemCount(3))
        onView(withId(R.id.upcoming_elections_header)).check(matches(isDisplayed()))
        onView(withId(R.id.upcoming_elections_header)).check(matches(withText("Upcoming elections")))
        onView(withId(R.id.saved_elections_header)).check(matches(isDisplayed()))
        onView(withId(R.id.saved_elections_header)).check(matches(withText("Saved elections")))
        onView(withId(R.id.saved_elections_recycler)).check(matches(isDisplayed()))
        onView(withId(R.id.saved_elections_recycler))
                .check(RecyclerViewItemCountAssertion.withItemCount(1))
    }

    @Test
    fun navigate_OpenVoterInfoAfterUpcomingElectionClicked() = runBlockingTest {
        val election1 = Election(1, "Title1", Date(), Division("1", "us", "al"))
        val election2 = Election(2, "Title2", Date(), Division("2", "us", "ga"))
        val election3 = Election(3, "Title3", Date(), Division("3", "us", "cl"), saved = true)
        repository.addElections(election1, election2, election3)

        val scenario = launchFragmentInContainer<ElectionsFragment>(null, R.style.AppTheme)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.upcoming_elections_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition<ElectionListAdapter.ElectionViewHolder>(0, click()))

        verify(navController).navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election1.toDomainModel()))
    }

    @Test
    fun navigate_OpenVoterInfoAfterSavedElectionClicked() = runBlockingTest {
        val election1 = Election(1, "Title1", Date(), Division("1", "us", "al"))
        val election2 = Election(2, "Title2", Date(), Division("2", "us", "ga"))
        val election3 = Election(3, "Title3", Date(), Division("3", "us", "cl"), saved = true)
        repository.addElections(election1, election2, election3)

        val scenario = launchFragmentInContainer<ElectionsFragment>(null, R.style.AppTheme)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.saved_elections_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition<ElectionListAdapter.ElectionViewHolder>(0, click()))

        verify(navController).navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election3.toDomainModel()))
    }

    @Test
    fun refresh_RemoveSavedListWhenElectionIsNotSavedAnymore() {
        val election1 = Election(1, "Title1", Date(), Division("1", "us", "al"))
        val election2 = Election(2, "Title2", Date(), Division("2", "us", "ga"))
        val election3 = Election(3, "Title3", Date(), Division("3", "us", "cl"), saved = true)
        repository.addElections(election1, election2, election3)

        val scenario = launchFragmentInContainer<ElectionsFragment>(null, R.style.AppTheme)

        onView(withId(R.id.saved_elections_header)).check(matches(isDisplayed()))
        onView(withId(R.id.saved_elections_recycler))
                .check(RecyclerViewItemCountAssertion.withItemCount(1))

        repository.removeAllElections()
        repository.addElections(election1, election2, election3.copy(saved = false))

        onView(withId(R.id.upcoming_refresh)).perform(swipeDown())

        onView(withId(R.id.saved_elections_header)).check(matches(not(isDisplayed())))
        onView(withId(R.id.saved_elections_recycler))
                .check(RecyclerViewItemCountAssertion.withItemCount(0))
    }
}

private fun Election.toDomainModel() =
        ElectionModel(
                id,
                name,
                electionDay,
                division,
                saved
        )

