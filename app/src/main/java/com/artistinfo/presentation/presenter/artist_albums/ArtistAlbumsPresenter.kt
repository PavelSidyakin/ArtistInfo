package com.artistinfo.presentation.presenter.artist_albums

import android.arch.paging.PagedList
import android.arch.paging.RxPagedListBuilder
import android.support.annotation.VisibleForTesting
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.artistinfo.domain.ArtistInfoInteractor
import com.artistinfo.model.AlbumListItem
import com.artistinfo.model.RequestAlbumsResultCode
import com.artistinfo.presentation.view.artist_albums.ArtistAlbumsView
import com.artistinfo.utils.logs.log
import com.artistinfo.utils.rx.SchedulersProvider
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.CompletableSubject

@InjectViewState
class ArtistAlbumsPresenter(
    val artistInfoInteractor: ArtistInfoInteractor,
    val schedulersProvider: SchedulersProvider
): MvpPresenter<ArtistAlbumsView>()  {

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val pageListConfig = PagedList.Config.Builder()
        .setPageSize(PAGE_SIZE)
        .setInitialLoadSizeHint(INITIAL_PAGE_SIZE)
        .setEnablePlaceholders(false)
        .build()

    @VisibleForTesting
    val requestCompletedCompletable: CompletableSubject = CompletableSubject.create()

    private var retryObservableEmitter: ObservableEmitter<Any>? = null
    private var retryObservable: Observable<Any> = Observable.create {emitter ->  retryObservableEmitter = emitter }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        hideAllErrors()
        showProgress(false)
    }

    fun setArtistId(artistId: Int) {
        performRequest(artistId)
            .subscribeOn(schedulersProvider.main())
            .subscribe( { pagedList ->
                viewState.updateDisplayData(pagedList)
                requestCompletedCompletable.onComplete()
            }, { throwable ->
                log { w(TAG, "", throwable) }
                requestCompletedCompletable.onError(throwable)
            }
            )
            .addTo(compositeDisposable)
    }

    private fun performRequest(artistId: Int): Observable<PagedList<AlbumListItem>> {
        return Observable.fromCallable {
                ArtistAlbumsDataSourceFactory(artistId, retryObservable, compositeDisposable, this, artistInfoInteractor, schedulersProvider)
            }
            .flatMap { artistAlbumsDataSourceFactory ->
                RxPagedListBuilder<Int, AlbumListItem>(artistAlbumsDataSourceFactory, pageListConfig)
                    .setNotifyScheduler(schedulersProvider.main())
                    .setFetchScheduler(schedulersProvider.io())
                    .buildObservable()
            }
            .doOnSubscribe { viewState.showProgress(true) }
            .doFinally { viewState.showProgress(false) }
            .doOnSubscribe { log { i(TAG, "ArtistAlbumsPresenter.performRequest(): Subscribe. artistId = [${artistId}]") } }
            .doOnNext { log { i(TAG, "ArtistAlbumsPresenter.performRequest(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "ArtistAlbumsPresenter.performRequest(): Error", it) } }
    }

    fun showProgress(show: Boolean) {
        Single.fromCallable { viewState.showProgress(show) }
            .subscribeOn(schedulersProvider.main())
            .subscribe( {}, {} )
            .addTo(compositeDisposable)
    }

    // Data source callbacks
    fun onResult(requestAlbumsResultCode: RequestAlbumsResultCode) {
        when(requestAlbumsResultCode) {
            RequestAlbumsResultCode.OK -> hideAllErrors()
            RequestAlbumsResultCode.NO_NETWORK -> viewState.showNoNetworkError(true)
            RequestAlbumsResultCode.GENERAL_ERROR -> viewState.showGeneralError(true)
        }

    }
    // ^ Data source callbacks

    private fun hideAllErrors() {
        viewState.showGeneralError(false)
        viewState.showNoNetworkError(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    fun onAlbumClicked(item: AlbumListItem) {
        viewState.openAlbumDetails(item.id)
    }

    fun onErrorClicked() {
        retryObservableEmitter?.onNext(Any())
    }

    companion object {
        private const val TAG = "ArtistAlbumsPresenter"

        @VisibleForTesting
        const val PAGE_SIZE = 8
        @VisibleForTesting
        const val INITIAL_PAGE_SIZE = PAGE_SIZE * 2
    }

}