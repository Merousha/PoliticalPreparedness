package com.udacity.example.politicalpreparedness.election

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.example.politicalpreparedness.BaseFragmentTest
import com.udacity.example.politicalpreparedness.R
import com.udacity.example.politicalpreparedness.election.model.ElectionModel
import com.udacity.example.politicalpreparedness.network.models.Division
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import java.util.*

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class VoterInfoFragmentTest : BaseFragmentTest() {

    @Test
    fun loadDetails_ShowVoterDetailsForUpcomingElection() = runBlockingTest {
        val election = ElectionModel(1, "Title1", Date(), Division("1", "us", "al"), false)
        val extras = bundleOf("election" to election)
        launchFragmentInContainer<VoterInfoFragment>(extras, R.style.AppTheme)

        onView(withId(R.id.state_header)).check(matches(isDisplayed()))
        onView(withId(R.id.state_ballot)).check(matches(isDisplayed()))
        onView(withId(R.id.state_locations)).check(matches(isDisplayed()))
        onView(withId(R.id.state_correspondence_header)).check(matches(isDisplayed()))
        onView(withId(R.id.address)).check(matches(isDisplayed()))
        onView(withId(R.id.state_header)).check(matches(isDisplayed()))
        onView(withId(R.id.state_header)).check(matches(withText("Alabama")))
        onView(withId(R.id.election_name)).check(matches(isDisplayed()))
        onView(withId(R.id.election_name)).check(matches(hasDescendant(withText("Title1"))))
        onView(withId(R.id.voter_action_button)).check(matches(withText("Follow election")))
    }

    @Test
    fun loadDetails_FailedShowVoterDetailsUpcomingElection() = runBlockingTest {
        val election = ElectionModel(1, "Title3", Date(), Division("1", "us", "al"), false)
        val extras = bundleOf("election" to election)
        repository.setReturnError(true)
        launchFragmentInContainer<VoterInfoFragment>(extras, R.style.AppTheme)

        onView(withId(R.id.state_header)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.state_ballot)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.state_locations)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.state_correspondence_header)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.address)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.state_header)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.election_name)).check(matches(isDisplayed()))
        onView(withId(R.id.election_name)).check(matches(hasDescendant(withText("Title3"))))
        onView(withText("Failed to load details using your location information")).check(matches(isDisplayed()))
    }

    @Test
    fun loadDetails_ShowVoterDetailsUpcomingAndSavedElection() = runBlockingTest {
        val election = ElectionModel(1, "Title3", Date(), Division("1", "us", "al"), true)
        val extras = bundleOf("election" to election)
        launchFragmentInContainer<VoterInfoFragment>(extras, R.style.AppTheme)

        onView(withId(R.id.state_header)).check(matches(isDisplayed()))
        onView(withId(R.id.state_ballot)).check(matches(isDisplayed()))
        onView(withId(R.id.state_locations)).check(matches(isDisplayed()))
        onView(withId(R.id.state_correspondence_header)).check(matches(isDisplayed()))
        onView(withId(R.id.address)).check(matches(isDisplayed()))
        onView(withId(R.id.state_header)).check(matches(isDisplayed()))
        onView(withId(R.id.election_name)).check(matches(isDisplayed()))
        onView(withId(R.id.election_name)).check(matches(hasDescendant(withText("Title3"))))
        onView(withId(R.id.voter_action_button)).check(matches(withText("Unfollow election")))
    }

    @Test
    fun followElection_NavigateBackOnClick() = runBlockingTest {
        val election = ElectionModel(1, "Title3", Date(), Division("1", "us", "al"), false)
        val extras = bundleOf("election" to election)
        val scenario = launchFragmentInContainer<VoterInfoFragment>(extras, R.style.AppTheme)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.voter_action_button)).check(matches(withText("Follow election")))
        onView(withId(R.id.voter_action_button)).perform(click())

        verify(navController).popBackStack()
    }

    @Test
    fun unfollowElection_NavigateBackOnClick() = runBlockingTest {
        val election = ElectionModel(1, "Title3", Date(), Division("1", "us", "al"), true)
        val extras = bundleOf("election" to election)
        val scenario = launchFragmentInContainer<VoterInfoFragment>(extras, R.style.AppTheme)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        onView(withId(R.id.voter_action_button)).check(matches(withText("Unfollow election")))
        onView(withId(R.id.voter_action_button)).perform(click())

        verify(navController).popBackStack()
    }
}