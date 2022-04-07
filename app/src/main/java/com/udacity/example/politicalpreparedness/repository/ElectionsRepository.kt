package com.udacity.example.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.udacity.example.politicalpreparedness.network.models.Address
import com.udacity.example.politicalpreparedness.network.models.Election
import com.udacity.example.politicalpreparedness.network.models.State
import com.udacity.example.politicalpreparedness.representative.model.Representative

interface ElectionsRepository {
    suspend fun getElections(force: Boolean): Result<List<Election>>
    suspend fun refreshElections()
    fun observeElections(): LiveData<Result<List<Election>>>
    suspend fun markAsSaved(election: Election, saved: Boolean)
    suspend fun getElectionDetails(electionId: Int, address: String): Result<State?>
    suspend fun searchRepresentatives(address: Address): Result<List<Representative>>
}