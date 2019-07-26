package com.artistinfo.presentation.presenter.search

import android.arch.paging.RxPagedListBuilder
import android.arch.paging.PagedList
import android.support.annotation.VisibleForTesting
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.artistinfo.domain.ArtistInfoInteractor
import com.artistinfo.model.ArtistListItem
import com.artistinfo.model.SearchArtistResultCode
import com.artistinfo.presentation.view.search.ArtistSearchView
import com.artistinfo.utils.logs.log
import com.artistinfo.utils.rx.SchedulersProvider
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.CompletableSubject
import java.util.concurrent.TimeUnit

@InjectViewState
class SearchArtistPresenter(
    val schedulersProvider: SchedulersProvider,
    val artistInfoInteractor: ArtistInfoInteractor

) : MvpPresenter<ArtistSearchView>()  {

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var performSearchObservableEmitter: ObservableEmitter<String>? = null
    private var performSearchObservable: Observable<String> = Observable.create {emitter ->  performSearchObservableEmitter = emitter }

    private var submitSearchObservableEmitter: ObservableEmitter<String>? = null
    private var submitSearchObservable: Observable<String> = Observable.create {emitter ->  submitSearchObservableEmitter = emitter }

    private var retryObservableEmitter: ObservableEmitter<Any>? = null
    private var retryObservable: Observable<Any> = Observable.create {emitter ->  retryObservableEmitter = emitter }

    private val pageListConfig = PagedList.Config.Builder()
        .setPageSize(PAGE_SIZE)
        .setInitialLoadSizeHint(INITIAL_PAGE_SIZE)
        .setEnablePlaceholders(false)
        .build()


    @VisibleForTesting
    var performSearchTimeoutMillis: Long = PERFORM_SEARCH_DEFAULT_TIMEOUT_MILLIS

    @VisibleForTesting
    val requestCompletedCompletable: CompletableSubject = CompletableSubject.create()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        Observable.merge(submitSearchObservable, performSearchObservable.debounce(performSearchTimeoutMillis, TimeUnit.MILLISECONDS))
            .flatMap { searchString -> preProcessInputString(searchString) }
            .doOnNext { searchString-> log { i(TAG, "Filtered search string: $searchString") } }
            .distinctUntilChanged()
            .switchMap { searchRequest -> performRequest(searchRequest) }
            .subscribeOn(schedulersProvider.main())
            .subscribe( { pagedList ->
                viewState.updateDisplayData(pagedList)
                requestCompletedCompletable.onComplete()
            }, { throwable ->
                log { w(TAG, "", throwable) }
                requestCompletedCompletable.onError(throwable)
            } )
            .addTo(compositeDisposable)

        hideAllErrors()
        viewState.showProgress(false)
    }

    private fun preProcessInputString(searchString: String): Observable<String> {
        return Observable.fromCallable { searchString.isEmpty() }
            .flatMap { isEmpty -> if (isEmpty) {
                    viewState.clearList()
                    Observable.never()
                } else {
                    Observable.just(searchString)
                }
            }
            .subscribeOn(schedulersProvider.main())
    }

    private fun performRequest(searchString: String): Observable<PagedList<ArtistListItem>> {
        return Observable.fromCallable {
                SearchArtistDataSourceFactory(searchString, retryObservable,this, compositeDisposable, artistInfoInteractor, schedulersProvider)
            }
            .flatMap { artistListDataSourceFactory ->
                RxPagedListBuilder<Int, ArtistListItem>(artistListDataSourceFactory, pageListConfig)
                    .setNotifyScheduler(schedulersProvider.main())
                    .setFetchScheduler(schedulersProvider.io())
                    .buildObservable()
            }
            .doOnSubscribe { log { i(TAG, "SearchArtistPresenter.performRequest(): Subscribe. searchString=$searchString") } }
            .doOnNext { log { i(TAG, "SearchArtistPresenter.performRequest(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "SearchArtistPresenter.performRequest(): Error", it) } }
    }

    fun onSearchTextChanged(searchText: String) {
        log { i(TAG, "Entered search string: $searchText") }
        performSearchObservableEmitter?.onNext(searchText)
    }

    fun onSearchTextSubmitted(searchText: String) {
        log { i(TAG, "Entered search string: $searchText") }
        submitSearchObservableEmitter?.onNext(searchText)
    }

    fun onErrorClicked() {
        retryObservableEmitter?.onNext(Any())
    }

    fun onArtistClicked(item: ArtistListItem) {
        viewState.openArtistAlbums(item.id)
    }

    // Data source callbacks

    fun onRequestStarted() {
        viewState.showProgress(true)
    }

    fun onResult(searchArtistResultCode: SearchArtistResultCode) {
        log { i(TAG, "onResult() $searchArtistResultCode")}

        viewState.showProgress(false)

        when(searchArtistResultCode) {
            SearchArtistResultCode.OK -> hideAllErrors()

            SearchArtistResultCode.NO_NETWORK -> viewState.showNoNetworkError(true)

            SearchArtistResultCode.GENERAL_ERROR -> viewState.showGeneralError(true)
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

    companion object {
        private const val TAG = "SearchArtistPresenter"
        private const val PERFORM_SEARCH_DEFAULT_TIMEOUT_MILLIS = 300L

        @VisibleForTesting
        const val PAGE_SIZE = 20
        @VisibleForTesting
        const val INITIAL_PAGE_SIZE = PAGE_SIZE * 2
    }
}