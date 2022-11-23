package com.project.samsin.forex.di

import com.project.samsin.forex.repo.ForexPairRepo
import com.project.samsin.forex.repo.ForexPairRepoImpl
import com.project.samsin.forex.usecase.WatchlistUseCase
import com.project.samsin.forex.usecase.WatchlistUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class WatchlistModule {
    @Binds
    abstract fun bindForexPairRepo(forexPairRepoImpl: ForexPairRepoImpl): ForexPairRepo

    @Binds
    abstract fun bindWatchlistUseCase(watchlistUseCaseImpl: WatchlistUseCaseImpl): WatchlistUseCase
}