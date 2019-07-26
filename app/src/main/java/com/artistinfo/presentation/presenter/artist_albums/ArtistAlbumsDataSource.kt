package com.artistinfo.presentation.presenter.artist_albums

import android.arch.paging.PageKeyedDataSource
import com.artistinfo.domain.ArtistInfoInteractor
import com.artistinfo.model.AlbumListItem
import com.artistinfo.model.RequestAlbumsResultCode
import com.artistinfo.utils.logs.log
import com.artistinfo.utils.rx.SchedulersProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.rxkotlin.addTo

class ArtistAlbumsDataSource(
    private val artistId: Int,
    private val retryObservable: Observable<Any>,
    private val compositeDisposable: CompositeDisposable,
    private val artistAlbumsPresenter: ArtistAlbumsPresenter,
    private val artistInfoInteractor: ArtistInfoInteractor,
    private val schedulersProvider: SchedulersProvider
    )  : PageKeyedDataSource<Int, AlbumListItem>()  {

    private var retryCompletable: Completable? = null

    init {
        retryObservable.subscribe {
            retry()
        }
            .addTo(compositeDisposable)
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, AlbumListItem>) {
        Single.defer { artistInfoInteractor.requestArtistAlbums(artistId, 0, params.requestedLoadSize) }
            .observeOn(schedulersProvider.main())
            .subscribeOn(schedulersProvider.io())
            .doOnSubscribe { artistAlbumsPresenter.showProgress(true) }
            .doFinally { artistAlbumsPresenter.showProgress(false) }
            .subscribe ( { requestAlbumsResult ->
                log { i(TAG, "ArtistAlbumsDataSource.loadInitial() result: $requestAlbumsResult") }
                if (requestAlbumsResult.requestAlbumsResultCode == RequestAlbumsResultCode.OK) {
                    requestAlbumsResult.albumList?.items?.let { list ->
                        callback.onResult(list, null, requestAlbumsResult.albumList.items.size)
                    }
                } else {
                    setRetryAction(Action { loadInitial(params, callback) })
                }
                artistAlbumsPresenter.onResult(requestAlbumsResult.requestAlbumsResultCode)
            }, { throwable ->
                log { w(TAG, "ArtistAlbumsDataSource.loadInitial()", throwable) }
                setRetryAction(Action { loadInitial(params, callback) })
                artistAlbumsPresenter.onResult(RequestAlbumsResultCode.GENERAL_ERROR)
            })
            .addTo(compositeDisposable)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, AlbumListItem>) {
        Single.defer { artistInfoInteractor.requestArtistAlbums(artistId, params.key, params.requestedLoadSize) }
            .observeOn(schedulersProvider.main())
            .subscribeOn(schedulersProvider.io())
            .doOnSubscribe { artistAlbumsPresenter.showProgress(true) }
            .doFinally { artistAlbumsPresenter.showProgress(false) }
            .subscribe ( { requestAlbumsResult ->
                log { i(TAG, "ArtistAlbumsDataSource.loadAfter() result: $requestAlbumsResult") }
                if (requestAlbumsResult.requestAlbumsResultCode == RequestAlbumsResultCode.OK ) {
                    requestAlbumsResult.albumList?.items?.let { list ->
                        callback.onResult(list, params.key + requestAlbumsResult.albumList.items.size)
                    }
                } else {
                    setRetryAction(Action { loadAfter(params, callback) })
                }
                artistAlbumsPresenter.onResult(requestAlbumsResult.requestAlbumsResultCode)
            }, { throwable ->
                log { w(TAG, "ArtistAlbumsDataSource.loadAfter()", throwable) }
                setRetryAction(Action { loadAfter(params, callback) })
                artistAlbumsPresenter.onResult(RequestAlbumsResultCode.GENERAL_ERROR)
            })
            .addTo(compositeDisposable)

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, AlbumListItem>) {
    }

    private fun setRetryAction(retryAction: Action?) {
        retryCompletable = if (retryAction == null) null else Completable.fromAction(retryAction)
    }

    private fun retry() {
        retryCompletable
            ?.subscribeOn(schedulersProvider.io())
            ?.observeOn(schedulersProvider.main())
            ?.subscribe()
            ?.addTo(compositeDisposable)
    }

    companion object {
        const val TAG = "ArtistAlbumsDataSource"
    }

}