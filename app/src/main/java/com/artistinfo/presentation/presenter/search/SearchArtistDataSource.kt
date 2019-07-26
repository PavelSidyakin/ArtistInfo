package com.artistinfo.presentation.presenter.search

import android.arch.paging.PageKeyedDataSource
import com.artistinfo.domain.ArtistInfoInteractor
import com.artistinfo.model.ArtistListItem
import com.artistinfo.model.SearchArtistResultCode
import com.artistinfo.utils.logs.log
import com.artistinfo.utils.rx.SchedulersProvider
import io.reactivex.rxkotlin.addTo
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action

class SearchArtistDataSource(
    private val searchRequest: String,
    private val retryObservable: Observable<Any>,
    private val searchArtistPresenter: SearchArtistPresenter,
    private val compositeDisposable: CompositeDisposable,
    private val artistInfoInteractor: ArtistInfoInteractor,
    private val schedulersProvider: SchedulersProvider
) : PageKeyedDataSource<Int, ArtistListItem>() {

    private var retryCompletable: Completable? = null

    init {
        retryObservable.subscribe {
            retry()
        }
        .addTo(compositeDisposable)
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ArtistListItem>) {
        Completable.fromCallable { searchArtistPresenter.onRequestStarted() }
            .andThen(Single.defer { artistInfoInteractor.searchArtists(searchRequest, 0, params.requestedLoadSize) })
            .subscribeOn(schedulersProvider.main())
            .observeOn(schedulersProvider.main())
            .subscribe ( { searchArtistResult ->
                log { i(TAG, "ArtistListDataSource.loadInitial() result: $searchArtistResult") }
                if (searchArtistResult.searchArtistResultCode == SearchArtistResultCode.OK) {
                    searchArtistResult.artistList?.items?.let { list ->
                        callback.onResult(list, null, searchArtistResult.artistList.items.size)
                    }
                } else {
                    setRetryAction(Action { loadInitial(params, callback) })
                }
                searchArtistPresenter.onResult(searchArtistResult.searchArtistResultCode)
            }, { throwable ->
                log { w(TAG, "ArtistListDataSource.loadInitial()", throwable) }
                setRetryAction(Action { loadInitial(params, callback) })
                searchArtistPresenter.onResult(SearchArtistResultCode.GENERAL_ERROR)
            })
            .addTo(compositeDisposable)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ArtistListItem>) {
        Completable.fromCallable { searchArtistPresenter.onRequestStarted() }
            .andThen(Single.defer { artistInfoInteractor.searchArtists(searchRequest, params.key, params.requestedLoadSize) })
            .subscribeOn(schedulersProvider.main())
            .observeOn(schedulersProvider.main())
            .subscribe ( { searchArtistResult ->
                log { i(TAG, "ArtistListDataSource.loadAfter() result: $searchArtistResult") }
                if (searchArtistResult.searchArtistResultCode == SearchArtistResultCode.OK) {
                    searchArtistResult.artistList?.items?.let { list ->
                        callback.onResult(list, params.key + searchArtistResult.artistList.items.size)
                    }
                } else {
                    setRetryAction(Action { loadAfter(params, callback) })
                }
                searchArtistPresenter.onResult(searchArtistResult.searchArtistResultCode)
            }, { throwable ->
                log { w(TAG, "ArtistListDataSource.loadAfter()", throwable) }
                setRetryAction(Action { loadAfter(params, callback) })
                searchArtistPresenter.onResult(SearchArtistResultCode.GENERAL_ERROR)
            })
            .addTo(compositeDisposable)
    }

    private fun setRetryAction(retryAction: Action?) {
        retryCompletable = if (retryAction == null) null else Completable.fromAction(retryAction)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ArtistListItem>) {
    }

    fun retry() {
        retryCompletable
            ?.subscribeOn(schedulersProvider.io())
            ?.observeOn(schedulersProvider.main())
            ?.subscribe()
            ?.addTo(compositeDisposable)
    }

    companion object {
        const val TAG = "ArtistListDataSource"
    }
}