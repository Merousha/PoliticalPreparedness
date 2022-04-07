package com.udacity.example.politicalpreparedness.network.models

import com.squareup.moshi.JsonClass
import com.udacity.example.politicalpreparedness.network.models.Election

@JsonClass(generateAdapter = true)
data class ElectionResponse(
        val kind: String,
        val elections: List<Election>
)