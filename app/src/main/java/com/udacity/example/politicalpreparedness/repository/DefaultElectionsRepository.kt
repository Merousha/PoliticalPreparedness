package com.udacity.example.politicalpreparedness.repository

import com.udacity.example.politicalpreparedness.network.models.Address
import com.udacity.example.politicalpreparedness.network.models.Election
import com.udacity.example.politicalpreparedness.network.models.ErrorResponse
import com.udacity.example.politicalpreparedness.network.models.State
import com.udacity.example.politicalpreparedness.representative.model.Representative
import com.udacity.example.politicalpreparedness.utils.wrapEspressoIdlingResource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.HttpException


class DefaultElectionsRepository(
    private val localDataSource: ElectionDataSource,
    private val networkDataSource: ElectionDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : ElectionsRepository {

    override fun observeElections() = localDataSource.observerElections()

    override suspend fun getElections(force: Boolean): Result<List<Election>> {
        wrapEspressoIdlingResource {
            if (force) {
                try {
                    getElectionsFromNetwork()
                } catch (exception: java.lang.Exception) {
                    return Result.Failure(exception)
                }
            }
            return localDataSource.getElections()
        }
    }

    private suspend fun getElectionsFromNetwork() {
        wrapEspressoIdlingResource {
            try {
                val elections = networkDataSource.getElections()
                if (elections is Result.Success) {
                    val localElections = localDataSource.getElections()
                    localDataSource.deleteAll()
                    if (localElections is Result.Success) {
                        val saved = localElections.data.filter { it.saved }
                        elections.data.map { online ->
                            online.copy(saved = saved.firstOrNull { it.id == online.id }?.saved == true)
                        }
                    } else {
                        elections.data
                    }.run { localDataSource.saveElections(this) }
                } else if (elections is Result.Failure) {
                    throw elections.exception
                }
            } catch (ex: Exception) {
                throw ex
            }
        }
    }

    override suspend fun refreshElections() {
        try {
            getElectionsFromNetwork()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override suspend fun markAsSaved(election: Election, saved: Boolean) {
        withContext(ioDispatcher) {
            coroutineScope {
                localDataSource.markAsSaved(election.copy(saved = saved))
            }
        }
    }

    override suspend fun getElectionDetails(electionId: Int, address: String): Result<State?> {
        wrapEspressoIdlingResource {
            return withContext(ioDispatcher) {
                networkDataSource.getDetails(electionId, address)
            }
        }
    }

    override suspend fun searchRepresentatives(address: Address): Result<List<Representative>> {
        wrapEspressoIdlingResource {
            return withContext(ioDispatcher) {
                when (val response = networkDataSource.getRepresentatives(address)) {
                    is Result.Failure -> Result.Failure(mapErrorResponse(response.exception))
                    is Result.Success -> {
                        val officials = response.data.officials
                        val representatives = response.data.offices
                                .map {
                                    it.getRepresentatives(officials)
                                }.flatten()
                        Result.Success(representatives)
                    }
                    is Result.Loading -> Result.Loading()
                }
            }
        }
    }

    private fun mapErrorResponse(failure: java.lang.Exception): Exception {
        return when (failure) {
            is HttpException -> {
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val jsonAdapter = moshi.adapter(ErrorResponse::class.java)
                val error = jsonAdapter.fromJson(requireNotNull(failure.response()?.errorBody()?.string()))
                IllegalStateException(error?.error?.message)
            }
            else -> failure
        }
    }
}