package com.udacity.example.politicalpreparedness.network.models

import com.udacity.example.politicalpreparedness.network.models.AdministrationBody

data class State (
    val name: String,
    val electionAdministrationBody: AdministrationBody
)