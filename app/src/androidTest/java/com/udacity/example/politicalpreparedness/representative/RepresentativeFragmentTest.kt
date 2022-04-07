package com.udacity.example.politicalpreparedness.representative

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.example.politicalpreparedness.BaseFragmentTest
import com.udacity.example.politicalpreparedness.R
import com.udacity.example.politicalpreparedness.util.RecyclerViewItemCountAssertion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.not
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class RepresentativeFragmentTest : BaseFragmentTest() {

    @Test
    fun searchRepresentative_ErrorMissingLine1() = runBlockingTest {
        launchFragmentInContainer<RepresentativeFragment>(null, R.style.AppTheme)

        onView(withId(R.id.button_search)).perform(click())
        onView(withText("Enter first line address")).check(matches(isDisplayed()))
    }

    @Test
    fun searchRepresentative_ErrorMissingCity() = runBlockingTest {
        launchFragmentInContainer<RepresentativeFragment>(null, R.style.AppTheme)

        onView(withId(R.id.address_line_1)).perform(typeText("Address line 1"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.button_search)).perform(click())
        onView(withText("Enter city")).check(matches(isDisplayed()))
    }

    @Test
    fun searchRepresentative_ErrorMissingZip() = runBlockingTest {
        launchFragmentInContainer<RepresentativeFragment>(null, R.style.AppTheme)

        onView(withId(R.id.address_line_1)).perform(typeText("Address line 1"))
        onView(withId(R.id.city)).perform(typeText("City Name"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.button_search)).perform(click())
        onView(withText("Enter zip code")).check(matches(isDisplayed()))
    }

    @Test
    fun searchRepresentative_ManuallyEnteredData() = runBlockingTest {
        launchFragmentInContainer<RepresentativeFragment>(null, R.style.AppTheme)

        onView(withId(R.id.address_line_1)).perform(typeText("Address line 1"))
        onView(withId(R.id.city)).perform(typeText("City Name"))
        onView(withId(R.id.zip)).perform(typeText("12345"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.button_search)).perform(click())
        onView(withId(R.id.representative_recycler))
                .check(RecyclerViewItemCountAssertion.withItemCount(4))
    }

    @Test
    fun searchRepresentative_LocationAddress() = runBlockingTest {
        launchFragmentInContainer<RepresentativeFragment>(null, R.style.AppTheme)

        onView(withId(R.id.button_location)).perform(click())
        onView(withId(R.id.representative_recycler))
                .check(RecyclerViewItemCountAssertion.withItemCount(4))
        onView(withId(R.id.representative_recycler)).perform(swipeUp())
        onView(withId(R.id.button_location)).check(matches(not(isDisplayed())))
    }

    @Test
    fun searchRepresentative_FetchingError() = runBlockingTest {
        repository.setReturnError(true)
        launchFragmentInContainer<RepresentativeFragment>(null, R.style.AppTheme)

        onView(withId(R.id.button_location)).perform(click())
        onView(withId(R.id.representative_recycler))
                .check(RecyclerViewItemCountAssertion.withItemCount(0))
        onView(withText("Test exception")).check(matches(isDisplayed()))
    }
}