package com.udacity.example.politicalpreparedness

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.example.politicalpreparedness.repository.ElectionsRepository
import com.udacity.example.politicalpreparedness.repository.Result
import com.udacity.example.politicalpreparedness.representative.model.Representative
import com.udacity.example.politicalpreparedness.network.models.*
import kotlinx.coroutines.runBlocking

class FakeTestRepository : ElectionsRepository {

    private var shouldReturnError = false
    var electionsServiceData: LinkedHashMap<Int, Election> = LinkedHashMap()

    private val observableElections = MutableLiveData<Result<List<Election>>>()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getElections(force: Boolean): Result<List<Election>> {
        if (shouldReturnError) {
            return Result.Failure(Exception("Test exception"))
        }
        return Result.Success(electionsServiceData.values.toList())
    }

    override suspend fun refreshElections() {
        observableElections.value = getElections(true)
    }

    override fun observeElections(): LiveData<Result<List<Election>>> {
        runBlocking { refreshElections() }
        return observableElections
    }

    override suspend fun markAsSaved(election: Election, saved: Boolean) {
        electionsServiceData[election.id] = election.copy(saved = saved)
    }

    fun addElections(vararg election: Election) {
        election.forEach {
            electionsServiceData[it.id] = it
        }
    }

    fun removeAllElections() {
        electionsServiceData = linkedMapOf()
    }

    override suspend fun getElectionDetails(electionId: Int, address: String): Result<State?> {
        return if (shouldReturnError) {
            Result.Failure(Exception("Test exception"))
        } else {
            Result.Success(
                    fakeStateData()
            )
        }
    }

    override suspend fun searchRepresentatives(address: Address): Result<List<Representative>> {
        return if (shouldReturnError) {
            Result.Failure(Exception("Test exception"))
        } else {
            Result.Success(
                    listOf(
                            fakeRepresentative("Fake official 1"),
                            fakeRepresentative("Fake official 2"),
                            fakeRepresentative("Fake official 3"),
                            fakeRepresentative("Fake official 4"),
                    )
            )
        }
    }

    private fun fakeRepresentative(officialName: String) = Representative(
            Official(officialName, emptyList(), "Fake party 1", listOf("111 332 543", "423125523"), listOf("https://www.google.com"), null, null),
            Office("Fake office", Division("Fake division-id", "US", "Alabama"), emptyList())
    )

    private fun fakeStateData() = State(
            name = "Alabama",
            electionAdministrationBody = AdministrationBody(
                    name = "Administration name",
                    electionInfoUrl = "https://www.google.com/info_url",
                    votingLocationFinderUrl = "https://www.google.com/votin_location_url",
                    ballotInfoUrl = "https://www.google.com/ballon_info_url",
                    correspondenceAddress = Address(
                            line1 = "Line one address",
                            line2 = "Line 2 address",
                            city = "City",
                            state = "State",
                            zip = "471283"
                    )
            )
    )
}