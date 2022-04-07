package com.udacity.example.politicalpreparedness.network.models

import com.squareup.moshi.JsonClass
import com.udacity.example.politicalpreparedness.network.models.Error

@JsonClass(generateAdapter = true)
data class ErrorResponse(
        val error: Error
)