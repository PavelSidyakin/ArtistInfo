package com.artistinfo.di

import com.artistinfo.TheApplication
import com.artistinfo.presentation.presenter.search.SearchArtistPresenter
import com.artistinfo.presentation.view.MainActivity
import com.artistinfo.presentation.view.album.AlbumDetailsFragment
import com.artistinfo.presentation.view.artist_albums.ArtistAlbumsViewFragment
import com.artistinfo.presentation.view.search.ArtistSearchViewFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(mapsActivity: TheApplication)
    fun inject(mapsActivity: MainActivity)
    fun inject(artistSearchViewFragment: ArtistSearchViewFragment)
    fun inject(searchArtistPresenter: SearchArtistPresenter)
    fun inject(artistAlbumsViewFragment: ArtistAlbumsViewFragment)
    fun inject(albumDetailsFragment: AlbumDetailsFragment)

    interface Builder {
        fun build(): AppComponent
    }
}