package com.artistinfo.presentation.presenter.artist_albums

import android.arch.paging.DataSource
import com.artistinfo.domain.ArtistInfoInteractor
import com.artistinfo.model.AlbumListItem
import com.artistinfo.utils.rx.SchedulersProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class ArtistAlbumsDataSourceFactory(
    private val artistId: Int,
    private val retryObservable: Observable<Any>,
    private val compositeDisposable: CompositeDisposable,
    private val artistAlbumsPresenter: ArtistAlbumsPresenter,
    private val artistInfoInteractor: ArtistInfoInteractor,
    private val schedulersProvider: SchedulersProvider
) : DataSource.Factory<Int, AlbumListItem>() {

    override fun create(): DataSource<Int, AlbumListItem> {
        return ArtistAlbumsDataSource(artistId, retryObservable, compositeDisposable, artistAlbumsPresenter, artistInfoInteractor, schedulersProvider)
    }
}