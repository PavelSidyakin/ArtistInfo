package com.artistinfo.presentation.presenter.album

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.artistinfo.domain.ArtistInfoInteractor
import com.artistinfo.model.RequestAlbumTracksResult
import com.artistinfo.model.RequestAlbumTracksResultCode
import com.artistinfo.presentation.view.album.AlbumDetailsView
import com.artistinfo.utils.logs.log
import com.artistinfo.utils.rx.SchedulersProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

@InjectViewState
class AlbumDetailsPresenter(
    val artistInfoInteractor: ArtistInfoInteractor,
    val schedulersProvider: SchedulersProvider
) : MvpPresenter<AlbumDetailsView>() {

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var currentAlbumId: Int = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        hideAllErrors()
        showProgress(false)
    }

    fun setAlbumId(albumId: Int) {
        currentAlbumId = albumId
        requestAlbumInfo(albumId)
    }

    private fun requestAlbumInfo(albumId: Int) {
        artistInfoInteractor.requestAlbumTracks(albumId)
            .doOnSubscribe { showProgress(true) }
            .doFinally { showProgress(false) }
            .doOnSubscribe { log { i(TAG, "AlbumDetailsPresenter.setAlbumId(): Subscribe. albumId = [${albumId}]") } }
            .doOnSuccess { log { i(TAG, "AlbumDetailsPresenter.setAlbumId(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "AlbumDetailsPresenter.setAlbumId(): Error", it) } }
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.main())
            .subscribe( {requestAlbumTracksResult ->

                when(requestAlbumTracksResult.resultCode) {
                    RequestAlbumTracksResultCode.OK -> processSuccessResult(requestAlbumTracksResult)
                    RequestAlbumTracksResultCode.NO_NETWORK -> viewState.showNoNetworkError(true)
                    RequestAlbumTracksResultCode.GENERAL_ERROR -> viewState.showGeneralError(true)
                }
            } , { })
            .addTo(compositeDisposable)
    }

    private fun processSuccessResult(requestAlbumTracksResult: RequestAlbumTracksResult) {
        requestAlbumTracksResult.albumTitle?.let { viewState.setAlbumTitle(it) }
        requestAlbumTracksResult.contributors?.let { viewState.setAlbumContributors(it) }
        viewState.setAlbumPicture(requestAlbumTracksResult.pictureUrl)
        requestAlbumTracksResult.trackList?.let { viewState.setTracksData(it) }
        hideAllErrors()
    }

    private fun hideAllErrors() {
        viewState.showGeneralError(false)
        viewState.showNoNetworkError(false)
    }

    fun showProgress(show: Boolean) {
        Single.fromCallable { viewState.showProgress(show) }
            .subscribeOn(schedulersProvider.main())
            .subscribe( {}, {} )
            .addTo(compositeDisposable)
    }

    fun onErrorClicked() {
        requestAlbumInfo(currentAlbumId)
    }

    companion object {
        private const val TAG = "AlbumDetailsPresenter"
    }

}