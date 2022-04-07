package com.udacity.example.politicalpreparedness.network.models

import com.udacity.example.politicalpreparedness.network.models.Address
import com.udacity.example.politicalpreparedness.network.models.Channel

data class Official (
    val name: String,
    val address: List<Address>? = null,
    val party: String? = null,
    val phones: List<String>? = null,
    val urls: List<String>? = null,
    val photoUrl: String? = null,
    val channels: List<Channel>? = null
)