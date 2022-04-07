package com.udacity.example.politicalpreparedness.network.jsonadapter

import com.udacity.example.politicalpreparedness.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class ElectionAdapter {
    @FromJson
    fun divisionFromJson(ocdDivisionId: String): Division {
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val country = ocdDivisionId.substringAfter(countryDelimiter, "")
                .substringBefore("/")

        val state = if (ocdDivisionId.contains(stateDelimiter)) {
            ocdDivisionId.substringAfter(stateDelimiter, "")
                    .substringBefore("/")
        } else {
            "n/a"
        }
        return Division(ocdDivisionId, country, state)
    }

    @ToJson
    fun divisionToJson(division: Division): String {
        return division.id
    }
}