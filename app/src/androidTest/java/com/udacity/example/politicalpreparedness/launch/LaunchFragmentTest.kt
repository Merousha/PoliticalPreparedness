package com.udacity.example.politicalpreparedness.launch

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.example.politicalpreparedness.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class LaunchFragmentTest {

    private val navController = Mockito.mock(NavController::class.java)

    @Test
    fun navigateToUpcomingElections() = runBlockingTest {
        val scenario = launchFragmentInContainer<LaunchFragment>(null, R.style.AppTheme)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.upcoming_button)).perform(click())
        verify(navController).navigate(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment())
    }

    @Test
    fun navigateToRepresentatives() = runBlockingTest {
        val scenario = launchFragmentInContainer<LaunchFragment>(null, R.style.AppTheme)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.representative_button)).perform(click())
        verify(navController).navigate(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment())
    }
}