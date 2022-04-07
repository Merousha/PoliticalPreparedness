package com.udacity.example.politicalpreparedness.network

import com.udacity.example.politicalpreparedness.network.jsonadapter.DateAdapter
import com.udacity.example.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.udacity.example.politicalpreparedness.network.models.ElectionResponse
import com.udacity.example.politicalpreparedness.network.models.RepresentativeResponse
import com.udacity.example.politicalpreparedness.network.models.VoterInfoResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://www.googleapis.com/civicinfo/v2/"

private val moshi = Moshi.Builder()
        .add(ElectionAdapter())
        .add(DateAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(CivicsHttpClient.getClient())
        .baseUrl(BASE_URL)
        .build()

/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 */

interface CivicsApiService {

    @GET("elections")
    suspend fun getElections(): ElectionResponse

    @GET("voterinfo")
    suspend fun getVoterInfo(
            @Query("address") address: String,
            @Query("electionId") electionId: Int,
    ): VoterInfoResponse

    @GET("representatives")
    suspend fun getRepresentatives(@Query("address", encoded = true) address: String,): RepresentativeResponse
}

object CivicsApi {
    fun create(): CivicsApiService =
        retrofit.create(CivicsApiService::class.java)
}