package com.artistinfo.presentation.presenter.search

import android.arch.paging.DataSource
import com.artistinfo.domain.ArtistInfoInteractor
import com.artistinfo.model.ArtistListItem
import com.artistinfo.utils.rx.SchedulersProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class SearchArtistDataSourceFactory(
    private val searchRequest: String,
    private val retryObservable: Observable<Any>,
    private val searchArtistPresenter: SearchArtistPresenter,
    private val compositeDisposable: CompositeDisposable,
    private val artistInfoInteractor: ArtistInfoInteractor,
    private val schedulersProvider: SchedulersProvider
) : DataSource.Factory<Int, ArtistListItem>() {

    override fun create(): DataSource<Int, ArtistListItem> {
        val artistListDataSource =
            SearchArtistDataSource(searchRequest, retryObservable, searchArtistPresenter, compositeDisposable, artistInfoInteractor, schedulersProvider)
        return artistListDataSource
    }
}