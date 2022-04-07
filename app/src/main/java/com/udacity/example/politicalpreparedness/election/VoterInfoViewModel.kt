package com.udacity.example.politicalpreparedness.election

import android.location.Address
import androidx.lifecycle.*
import com.udacity.example.politicalpreparedness.R
import com.udacity.example.politicalpreparedness.election.model.ElectionModel
import com.udacity.example.politicalpreparedness.election.model.toDataModel
import com.udacity.example.politicalpreparedness.network.models.State
import com.udacity.example.politicalpreparedness.repository.ElectionsRepository
import com.udacity.example.politicalpreparedness.repository.Result
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val repository: ElectionsRepository,
    val election: ElectionModel,
) : ViewModel() {

    val errorMessage: LiveData<Int?>
        get() = Transformations.map(_electionDetails) {
            if (it is Result.Failure) {
                R.string.error_failed_load_voter_info
            } else {
                null
            }
        }

    private val _electionDetails: MutableLiveData<Result<State?>> = MutableLiveData()
    val electionDetails: LiveData<State?> = Transformations.map(_electionDetails) {
        when (it) {
            is Result.Success -> it.data
            else -> null
        }
    }

    private val _navigateBack: MutableLiveData<Boolean> = MutableLiveData()
    val navigateBack: LiveData<Boolean>
        get() = _navigateBack

    fun loadDetails(address: Address?) {
        viewModelScope.launch {
            val exactAddress = "${address?.getAddressLine(0)}"
            val response = repository.getElectionDetails(election.id, exactAddress)
            _electionDetails.value = response
        }
    }

    fun onActionClick() {
        viewModelScope.launch {
            repository.markAsSaved(election.toDataModel(), election.saved.not())
            _navigateBack.value = true
        }
    }

    fun navigateCompleted() {
        _navigateBack.value = false
    }
}