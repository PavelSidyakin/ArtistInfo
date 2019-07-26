package com.artistinfo.di

import com.artistinfo.data.ApplicationProviderImpl
import com.artistinfo.data.DeezerArtistInfoRepositoryImpl
import com.artistinfo.domain.data.ApplicationProvider
import com.artistinfo.domain.ArtistInfoInteractor
import com.artistinfo.domain.ArtistInfoInteractorImpl
import com.artistinfo.domain.data.DeezerArtistInfoRepository
import com.artistinfo.utils.NetworkUtils
import com.artistinfo.utils.NetworkUtilsImpl
import com.artistinfo.utils.rx.SchedulersProvider
import com.artistinfo.utils.rx.SchedulersProviderImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun provideApplicationProvider(applicationProvider: ApplicationProviderImpl) : ApplicationProvider

    @Singleton
    @Binds
    abstract fun provideNetworkUtils(networkUtils: NetworkUtilsImpl) : NetworkUtils

    @Singleton
    @Binds
    abstract fun provideSchedulersProvider(schedulersProvider: SchedulersProviderImpl) : SchedulersProvider

    @Singleton
    @Binds
    abstract fun provideDeezerArtistInfoRepository(deezerArtistInfoRepositoryImpl: DeezerArtistInfoRepositoryImpl) : DeezerArtistInfoRepository

    @Singleton
    @Binds
    abstract fun provideArtistInfoInteractor(artistInfoInteractor: ArtistInfoInteractorImpl) : ArtistInfoInteractor

}