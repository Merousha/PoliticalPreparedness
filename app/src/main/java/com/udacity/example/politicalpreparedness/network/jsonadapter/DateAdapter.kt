package com.udacity.example.politicalpreparedness.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter {

    @FromJson
    fun dateFromJson(date: String): Date {
        return SimpleDateFormat("yyyy-MM-dd").parse(date)
    }

    @ToJson
    fun dateToJson(date: Date): String {
        return SimpleDateFormat("yyy-MM-dd").format(date)
    }
}