package com.udacity.example.politicalpreparedness.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.udacity.example.politicalpreparedness.repository.ElectionDataSource
import com.udacity.example.politicalpreparedness.repository.Result
import com.udacity.example.politicalpreparedness.network.models.*

class FakeDataSource(val elections: MutableList<Election>? = mutableListOf()) : ElectionDataSource {

    var showDetailsError = false

    override fun observerElections(): LiveData<Result<List<Election>>> {
        return MutableLiveData(elections).map {
            Result.Success(it) as Result<List<Election>>
        }
    }

    override suspend fun getElections(): Result<List<Election>> {
        elections?.let { return Result.Success(ArrayList(it)) }
        return Result.Failure(
                Exception("Tasks not found")
        )
    }

    override suspend fun saveElections(elections: List<Election>) {
        this.elections?.addAll(elections)
    }

    override suspend fun markAsSaved(election: Election) {
        elections?.removeIf { it.id == election.id }
        elections?.add(election)
    }

    override suspend fun deleteAll() {
        this.elections?.clear()
    }

    override suspend fun getDetails(electionId: Int, address: String): Result<State?> {
        return if (showDetailsError) {
            Result.Failure(IllegalStateException("Failed to get details!"))
        } else {
            Result.Success(fakeStateData())
        }
    }

    override suspend fun getRepresentatives(address: Address): Result<RepresentativeResponse> {
        return if (showDetailsError) {
            Result.Failure(IllegalStateException("Failed to get details!"))
        } else {
            Result.Success(
                    RepresentativeResponse(
                            listOf(
                                    Office("Office 0", Division("1", "ZA", "WesternCape"), listOf(1, 2, 3)),
                                    Office("Office 1", Division("1", "ZA", "WesternCape"), listOf(1, 2, 3)),
                                    Office("Office 2", Division("1", "ZA", "WesternCape"), listOf(1, 2, 3)),
                                    Office("Office 2", Division("1", "ZA", "WesternCape"), listOf(1, 2, 3)),
                            ),
                            listOf(
                                    Official("Official 1", emptyList(), "Fake party 1", listOf("111 332 543", "111115523"), listOf("https://www.google.com"), null, null),
                                    Official("Official 2", emptyList(), "Fake party 2", listOf("111 332 543", "111115523"), listOf("https://www.google.com"), null, null),
                                    Official("Official 3", emptyList(), "Fake party 3", listOf("111 332 543", "111115523"), listOf("https://www.google.com"), null, null),
                                    Official("Official 4", emptyList(), "Fake party 4", listOf("111 332 543", "111115523"), listOf("https://www.google.com"), null, null),
                            )
                    )
            )
        }
    }

    private fun fakeStateData() = State(
            name = "WesternCape",
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