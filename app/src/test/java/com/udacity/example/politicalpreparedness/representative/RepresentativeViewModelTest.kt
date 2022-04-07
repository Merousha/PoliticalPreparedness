package com.udacity.example.politicalpreparedness.representative

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.udacity.example.politicalpreparedness.MainCoroutineRule
import com.udacity.example.politicalpreparedness.R
import com.udacity.example.politicalpreparedness.data.FakeTestRepository
import com.udacity.example.politicalpreparedness.getOrAwaitValue
import com.udacity.example.politicalpreparedness.network.models.Address
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsEmptyCollection
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.`is` as matchersIs

@ExperimentalCoroutinesApi
class RepresentativeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var electionsRepository: FakeTestRepository
    private lateinit var representativeViewModel: RepresentativeViewModel

    private val address: Address = Address("line 1", "line 2", "City", "State", "12345")

    @Before
    fun setupViewModel() {
        electionsRepository = FakeTestRepository()
        representativeViewModel = RepresentativeViewModel(electionsRepository)
    }

    @Test
    fun searchRepresentatives_ErrorMissingLine1() {
        representativeViewModel.searchForMyRepresentatives()

        assertThat(representativeViewModel.message.getOrAwaitValue(), matchersIs(R.string.error_missing_first_line_address))
    }

    @Test
    fun searchRepresentatives_ErrorMissingCity() {
        representativeViewModel.line1.value = "Address first line"

        representativeViewModel.searchForMyRepresentatives()

        assertThat(representativeViewModel.message.getOrAwaitValue(), matchersIs(R.string.error_missing_city))
    }

    @Test
    fun searchRepresentatives_ErrorMissingState() {
        representativeViewModel.line1.value = "Address first line"
        representativeViewModel.city.value = "Washington"

        representativeViewModel.searchForMyRepresentatives()

        assertThat(representativeViewModel.message.getOrAwaitValue(), matchersIs(R.string.error_missing_state))
    }

    @Test
    fun searchRepresentatives_ErrorMissingZipCode() {
        representativeViewModel.line1.value = "Address first line"
        representativeViewModel.city.value = "Washington"
        representativeViewModel.state.value = "Alabama"

        representativeViewModel.searchForMyRepresentatives()

        assertThat(representativeViewModel.message.getOrAwaitValue(), matchersIs(R.string.error_missing_zip))
    }

    @Test
    fun searchRepresentatives_ShowLoadingView() {
        representativeViewModel.line1.value = "Address first line"
        representativeViewModel.city.value = "Washington"
        representativeViewModel.state.value = "Alabama"
        representativeViewModel.zip.value = "12345"

        mainCoroutineRule.pauseDispatcher()

        representativeViewModel.searchForMyRepresentatives()

        assertThat(representativeViewModel.dataLoading.getOrAwaitValue(), matchersIs(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(representativeViewModel.dataLoading.getOrAwaitValue(), matchersIs(false))
    }

    @Test
    fun searchRepresentatives_ErrorLoadingData() {
        representativeViewModel.line1.value = "Address first line"
        representativeViewModel.city.value = "Washington"
        representativeViewModel.state.value = "Alabama"
        representativeViewModel.zip.value = "12345"
        electionsRepository.setReturnError(true)

        representativeViewModel.searchForMyRepresentatives()

        assertThat(representativeViewModel.messageString.getOrAwaitValue(), matchersIs("Test exception"))
        assertThat(representativeViewModel.representatives.getOrAwaitValue(), IsEmptyCollection())
    }

    @Test
    fun searchRepresentatives_ManuallyEnteredData() {
        representativeViewModel.line1.value = "Address first line"
        representativeViewModel.city.value = "Washington"
        representativeViewModel.state.value = "Alabama"
        representativeViewModel.zip.value = "12345"

        representativeViewModel.searchForMyRepresentatives()

        assertThat(representativeViewModel.representatives.getOrAwaitValue().size, matchersIs(4))
    }

    @Test
    fun searchRepresentatives_GeocodedAddress() {
        representativeViewModel.searchForRepresentatives(address)

        assertThat(representativeViewModel.representatives.getOrAwaitValue().size, matchersIs(4))
    }

    @Test
    fun updateState() {
        representativeViewModel.setState("New State")

        assertThat(representativeViewModel.state.getOrAwaitValue(), matchersIs("New State"))
    }
}