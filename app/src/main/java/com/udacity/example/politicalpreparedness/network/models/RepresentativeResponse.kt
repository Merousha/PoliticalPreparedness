package com.udacity.example.politicalpreparedness.network.models

import com.squareup.moshi.JsonClass
import com.udacity.example.politicalpreparedness.network.models.Office
import com.udacity.example.politicalpreparedness.network.models.Official

@JsonClass(generateAdapter = true)
data class RepresentativeResponse(
    val offices: List<Office>,
    val officials: List<Official>
)