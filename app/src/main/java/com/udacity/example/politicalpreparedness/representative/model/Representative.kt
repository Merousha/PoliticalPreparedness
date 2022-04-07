package com.udacity.example.politicalpreparedness.representative.model

import com.udacity.example.politicalpreparedness.network.models.Office
import com.udacity.example.politicalpreparedness.network.models.Official

data class Representative (
        val official: Official,
        val office: Office
)