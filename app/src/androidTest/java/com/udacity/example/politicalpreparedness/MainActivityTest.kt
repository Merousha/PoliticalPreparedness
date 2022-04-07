package com.udacity.example.politicalpreparedness

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.udacity.example.politicalpreparedness.database.ElectionDao
import com.udacity.example.politicalpreparedness.database.ElectionDatabase
import com.udacity.example.politicalpreparedness.database.LocalDataSource
import com.udacity.example.politicalpreparedness.election.ElectionsViewModel
import com.udacity.example.politicalpreparedness.election.VoterInfoViewModel
import com.udacity.example.politicalpreparedness.election.adapter.ElectionListAdapter
import com.udacity.example.politicalpreparedness.election.model.ElectionModel
import com.udacity.example.politicalpreparedness.network.CivicsApi
import com.udacity.example.politicalpreparedness.network.CivicsApiService
import com.udacity.example.politicalpreparedness.network.NetworkDataSource
import com.udacity.example.politicalpreparedness.repository.DefaultElectionsRepository
import com.udacity.example.politicalpreparedness.repository.ElectionDataSource
import com.udacity.example.politicalpreparedness.repository.ElectionsRepository
import com.udacity.example.politicalpreparedness.representative.RepresentativeViewModel
import com.udacity.example.politicalpreparedness.util.DataBindingIdlingResource
import com.udacity.example.politicalpreparedness.util.RecyclerViewItemCountAssertion
import com.udacity.example.politicalpreparedness.util.monitorActivity
import com.udacity.example.politicalpreparedness.utils.EspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest : AutoCloseKoinTest() {

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Rule
    @JvmField
    val mRuntimePermissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    @Before
    fun setUp() {
        stopKoin()
        val appContext: Application = getApplicationContext()
        val module = module {
            viewModel { (election: ElectionModel) ->
                VoterInfoViewModel(get(), election)
            }
            viewModel { ElectionsViewModel(get()) }
            viewModel { RepresentativeViewModel(get()) }
            single { ElectionDatabase.getInstance(appContext).electionDao as ElectionDao }
            single { CivicsApi.create() as CivicsApiService }
            single(qualifier = named("local")) { LocalDataSource(get(), Dispatchers.IO) as ElectionDataSource }
            single(qualifier = named("remote")) { NetworkDataSource(get(), Dispatchers.IO) as ElectionDataSource }
            single { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) as DateFormat }
            single {
                DefaultElectionsRepository(
                        get<ElectionDataSource>(qualifier = named("local")),
                        get<ElectionDataSource>(qualifier = named("remote")),
                        Dispatchers.IO,
                ) as ElectionsRepository
            }
        }
        startKoin {
            modules(listOf(module))
        }
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun upcomingElections_AddAndRemoveFromSaved() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.upcoming_button)).perform(click())

        addElectionToSaved()

        onView(withId(R.id.saved_elections_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition<ElectionListAdapter.ElectionViewHolder>(0, click()))
        onView(withId(R.id.voter_action_button)).check(matches(withText("Unfollow election")))
        onView(withId(R.id.voter_action_button)).perform(click())

        onView(withId(R.id.saved_elections_header)).check(matches(not(isDisplayed())))

        activityScenario.close()
    }

    @Test
    fun myRepresentative_SearchForMyRepresentativesUsingLocationAddress() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.upcoming_button)).perform(click())

        addElectionToSaved()
        Espresso.pressBack()

        onView(withId(R.id.representative_button)).perform(click())
        onView(withId(R.id.button_location)).perform(click())
        onView(withId(R.id.representative_recycler))
                .check(RecyclerViewItemCountAssertion.withItemCount(not(0)))

        Espresso.pressBack()

        onView(withId(R.id.upcoming_button)).perform(click())
        onView(withId(R.id.saved_elections_header)).check(ViewAssertions.matches(withText("Saved elections")))
        onView(withId(R.id.saved_elections_recycler)).check(matches(isDisplayed()))
        onView(withId(R.id.saved_elections_recycler))
                .check(RecyclerViewItemCountAssertion.withItemCount(1))

        activityScenario.close()
    }

    private fun addElectionToSaved() {
        onView(withId(R.id.upcoming_elections_header)).check(matches(isDisplayed()))
        onView(withId(R.id.upcoming_elections_header)).check(matches(withText("Upcoming elections")))
        onView(withId(R.id.saved_elections_header)).check(matches(not(isDisplayed())))
        onView(withId(R.id.upcoming_elections_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition<ElectionListAdapter.ElectionViewHolder>(0, click()))

        onView(withId(R.id.voter_action_button)).check(matches(withText("Follow election")))
        onView(withId(R.id.voter_action_button)).perform(click())

        onView(withId(R.id.saved_elections_header)).check(ViewAssertions.matches(withText("Saved elections")))
        onView(withId(R.id.saved_elections_recycler)).check(matches(isDisplayed()))
        onView(withId(R.id.saved_elections_recycler))
                .check(RecyclerViewItemCountAssertion.withItemCount(1))
    }
}