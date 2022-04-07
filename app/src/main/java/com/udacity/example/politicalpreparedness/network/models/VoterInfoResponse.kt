package com.udacity.example.politicalpreparedness.network.models

import com.squareup.moshi.JsonClass
import com.udacity.example.politicalpreparedness.network.models.Election
import com.udacity.example.politicalpreparedness.network.models.ElectionOfficial
import com.udacity.example.politicalpreparedness.network.models.State

@JsonClass(generateAdapter = true)
class VoterInfoResponse (
    val election: Election,
    val state: List<State>? = null,
    val electionElectionOfficials: List<ElectionOfficial>? = null
)