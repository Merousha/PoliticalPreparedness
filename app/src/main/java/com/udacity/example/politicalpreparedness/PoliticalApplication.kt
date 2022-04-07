package com.udacity.example.politicalpreparedness

import android.app.Application
import com.udacity.example.politicalpreparedness.database.ElectionDao
import com.udacity.example.politicalpreparedness.database.ElectionDatabase
import com.udacity.example.politicalpreparedness.database.LocalDataSource
import com.udacity.example.politicalpreparedness.election.ElectionsViewModel
import com.udacity.example.politicalpreparedness.election.VoterInfoViewModel
import com.udacity.example.politicalpreparedness.election.model.ElectionModel
import com.udacity.example.politicalpreparedness.network.CivicsApi
import com.udacity.example.politicalpreparedness.network.CivicsApiService
import com.udacity.example.politicalpreparedness.network.NetworkDataSource
import com.udacity.example.politicalpreparedness.repository.DefaultElectionsRepository
import com.udacity.example.politicalpreparedness.repository.ElectionDataSource
import com.udacity.example.politicalpreparedness.repository.ElectionsRepository
import com.udacity.example.politicalpreparedness.representative.RepresentativeViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class PoliticalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val module = module {
            viewModel { (election: ElectionModel) ->
                VoterInfoViewModel(get(), election)
            }
            viewModel { ElectionsViewModel(get()) }
            viewModel { RepresentativeViewModel(get()) }
            single { ElectionDatabase.getInstance(this@PoliticalApplication).electionDao as ElectionDao }
            single { CivicsApi.create() as CivicsApiService }
            single(qualifier = named("local")) { LocalDataSource(get(), Dispatchers.IO) as ElectionDataSource }
            single(qualifier = named("remote")) { NetworkDataSource(get(), Dispatchers.IO) as ElectionDataSource }
            single { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) as DateFormat}
            single {
                DefaultElectionsRepository(
                        get<ElectionDataSource>(qualifier = named("local")),
                        get<ElectionDataSource>(qualifier = named("remote")),
                        Dispatchers.IO,
                ) as ElectionsRepository
            }
        }
        startKoin {
            androidContext(this@PoliticalApplication)
            modules(listOf(module))
        }
    }
}