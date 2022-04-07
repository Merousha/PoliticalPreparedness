package com.udacity.example.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.udacity.example.politicalpreparedness.network.models.Address
import com.udacity.example.politicalpreparedness.network.models.Election
import com.udacity.example.politicalpreparedness.network.models.RepresentativeResponse
import com.udacity.example.politicalpreparedness.network.models.State

interface ElectionDataSource {
    fun observerElections(): LiveData<Result<List<Election>>>
    suspend fun getElections(): Result<List<Election>>
    suspend fun saveElections(elections: List<Election>)
    suspend fun markAsSaved(election: Election)
    suspend fun deleteAll()
    suspend fun getDetails(electionId: Int, address: String): Result<State?>
    suspend fun getRepresentatives(address: Address): Result<RepresentativeResponse>
}