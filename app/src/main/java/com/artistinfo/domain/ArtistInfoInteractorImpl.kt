package com.artistinfo.domain

import com.artistinfo.domain.data.DeezerArtistInfoRepository
import com.artistinfo.model.AlbumList
import com.artistinfo.model.AlbumListItem
import com.artistinfo.model.ArtistList
import com.artistinfo.model.ArtistListItem
import com.artistinfo.model.RequestAlbumsResult
import com.artistinfo.model.RequestAlbumsResultCode
import com.artistinfo.model.RequestAlbumTracksResult
import com.artistinfo.model.RequestAlbumTracksResultCode
import com.artistinfo.model.SearchArtistResultCode
import com.artistinfo.model.SearchArtistResult
import com.artistinfo.model.TrackList
import com.artistinfo.model.TrackListItem
import com.artistinfo.model.deezer.DeezerAlbumRequestResult
import com.artistinfo.model.deezer.DeezerAlbumRequestResultCode
import com.artistinfo.model.deezer.DeezerAlbumTracksRequestResultCode
import com.artistinfo.model.deezer.DeezerArtistAlbumsRequestResultCode
import com.artistinfo.model.deezer.DeezerSearchArtistRequestResultCode
import com.artistinfo.model.deezer.DeezerTrackRequestResult
import com.artistinfo.model.deezer.DeezerTrackRequestResultCode
import com.artistinfo.model.deezer.rest.album_tracks.AlbumTracksDataItem
import com.artistinfo.model.deezer.rest.artist_albums.ArtistAlbumsDataItem
import com.artistinfo.utils.NetworkUtils
import com.artistinfo.utils.logs.log
import com.artistinfo.utils.rx.SchedulersProvider
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class ArtistInfoInteractorImpl @Inject constructor(
    private val deezerArtistInfoRepository: DeezerArtistInfoRepository,
    private val networkUtils: NetworkUtils,
    private val schedulersProvider: SchedulersProvider
) : ArtistInfoInteractor {

    override fun searchArtists(name: String, startIndex: Int, maxCount: Int): Single<SearchArtistResult> {
        return Single.fromCallable { networkUtils.networkConnectionOn }
            .flatMap { networkOn ->
                if (networkOn) {
                    deezerArtistInfoRepository.searchArtists(name, startIndex, maxCount)
                } else {
                    Single.error(ArtistInfoException(ArtistInfoErrorCode.NO_CONNECTION))
                }
            }
            .flatMap { deezerSearchArtistRequestResult ->
                if (deezerSearchArtistRequestResult.resultCode != DeezerSearchArtistRequestResultCode.OK) {
                    if (deezerSearchArtistRequestResult.resultCode == DeezerSearchArtistRequestResultCode.NETWORK_ERROR) {
                        Single.error(ArtistInfoException(ArtistInfoErrorCode.NO_CONNECTION))
                    } else {
                        Single.error(ArtistInfoException(ArtistInfoErrorCode.GENERAL_ERROR))
                    }
                } else {
                    if (deezerSearchArtistRequestResult.result != null) {
                        Single.just(deezerSearchArtistRequestResult.result)
                    } else {
                        Single.error(ArtistInfoException(ArtistInfoErrorCode.GENERAL_ERROR))
                    }
                }
            }
            .map { deezerSearchArtistResult -> deezerSearchArtistResult.data }
            .toObservable()
            .flatMapIterable { dataItems -> dataItems }
            .map { dataItem -> ArtistListItem(dataItem.id, dataItem.name, dataItem.picture_medium) }
            .toList()
            .map { artistListItems -> SearchArtistResult(SearchArtistResultCode.OK, ArtistList(artistListItems)) }
            .onErrorResumeNext { throwable ->
                if (throwable is ArtistInfoException && throwable.errorCode == ArtistInfoErrorCode.NO_CONNECTION) {
                    Single.just(SearchArtistResult(SearchArtistResultCode.NO_NETWORK, null))
                } else
                    Single.just(SearchArtistResult(SearchArtistResultCode.GENERAL_ERROR, null))
            }
            .subscribeOn(schedulersProvider.io())
            .doOnSubscribe { log { i(TAG, "ArtistInfoInteractorImpl.searchArtists(): Subscribe. name = [${name}], startIndex = [${startIndex}], maxCount = [${maxCount}]") } }
            .doOnSuccess { log { i(TAG, "ArtistInfoInteractorImpl.searchArtists(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "ArtistInfoInteractorImpl.searchArtists(): Error", it) } }
    }

    override fun requestArtistAlbums(artistId: Int, startIndex: Int, maxCount: Int): Single<RequestAlbumsResult> {
        return Single.fromCallable { networkUtils.networkConnectionOn }
            .flatMap { networkOn ->
                if (networkOn) {
                    deezerArtistInfoRepository.requestArtistAlbums(artistId, startIndex, maxCount)
                } else {
                    Single.error(ArtistInfoException(ArtistInfoErrorCode.NO_CONNECTION))
                }
            }
            .flatMap { deezerArtistAlbumsRequestResult ->
                if (deezerArtistAlbumsRequestResult.resultCode != DeezerArtistAlbumsRequestResultCode.OK) {
                    if (deezerArtistAlbumsRequestResult.resultCode == DeezerArtistAlbumsRequestResultCode.NETWORK_ERROR) {
                        Single.error(ArtistInfoException(ArtistInfoErrorCode.NO_CONNECTION))
                    } else {
                        Single.error(ArtistInfoException(ArtistInfoErrorCode.GENERAL_ERROR))
                    }
                } else {
                    if (deezerArtistAlbumsRequestResult.result != null) {
                        Single.just(deezerArtistAlbumsRequestResult.result)
                    } else {
                        Single.error(ArtistInfoException(ArtistInfoErrorCode.GENERAL_ERROR))
                    }
                }
            }
            .map { deezerSearchArtistResult -> deezerSearchArtistResult.data }
            .toObservable()
            .flatMapIterable { dataItems -> dataItems }
            .flatMap { dataItem ->
                Observable.just(dataItem)
                    .zipWith(deezerArtistInfoRepository.requestAlbum(dataItem.id).toObservable(),
                        BiFunction <ArtistAlbumsDataItem, DeezerAlbumRequestResult, AlbumListItem> {
                                dataItem2, album ->
                            if (album.resultCode == DeezerAlbumRequestResultCode.NETWORK_ERROR) {
                                throw ArtistInfoException(ArtistInfoErrorCode.NO_CONNECTION)
                            }
                            AlbumListItem(dataItem2.id, dataItem2.title, album.result?.contributors?.map { it.name }, dataItem2.cover_big)
                        }
                    )
                    .subscribeOn(schedulersProvider.io())
            }
            .toList()
            .map { albumList -> RequestAlbumsResult(RequestAlbumsResultCode.OK, AlbumList(albumList)) }
            .onErrorResumeNext { throwable ->
                if (throwable is ArtistInfoException && throwable.errorCode == ArtistInfoErrorCode.NO_CONNECTION) {
                    Single.just(RequestAlbumsResult(RequestAlbumsResultCode.NO_NETWORK, null))
                } else
                    Single.just(RequestAlbumsResult(RequestAlbumsResultCode.GENERAL_ERROR, null))
            }
            .subscribeOn(schedulersProvider.io())
            .doOnSubscribe { log { i(TAG, "ArtistInfoInteractorImpl.requestArtistAlbums(): Subscribe. artistId = [${artistId}], startIndex = [${startIndex}], maxCount = [${maxCount}]") } }
            .doOnSuccess { log { i(TAG, "ArtistInfoInteractorImpl.requestArtistAlbums(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "ArtistInfoInteractorImpl.requestArtistAlbums(): Error", it) } }
    }

    override fun requestAlbumTracks(albumId: Int): Single<RequestAlbumTracksResult> {
        return Single.fromCallable { networkUtils.networkConnectionOn }
            .flatMap { networkOn ->
                if (networkOn) {
                    deezerArtistInfoRepository.requestAlbumTracks(albumId, 0, Int.MAX_VALUE)
                } else {
                    Single.error(ArtistInfoException(ArtistInfoErrorCode.NO_CONNECTION))
                }
            }
            .flatMap { deezerAlbumTracksRequestResult ->
                if (deezerAlbumTracksRequestResult.resultCode != DeezerAlbumTracksRequestResultCode.OK) {
                    if (deezerAlbumTracksRequestResult.resultCode == DeezerAlbumTracksRequestResultCode.NETWORK_ERROR) {
                        Single.error(ArtistInfoException(ArtistInfoErrorCode.NO_CONNECTION))
                    } else {
                        Single.error(ArtistInfoException(ArtistInfoErrorCode.GENERAL_ERROR))
                    }
                } else {
                    if (deezerAlbumTracksRequestResult.result != null) {
                        Single.just(deezerAlbumTracksRequestResult.result)
                    } else {
                        Single.error(ArtistInfoException(ArtistInfoErrorCode.GENERAL_ERROR))
                    }
                }
            }
            .map { deezerAlbumTracksRequestResult -> deezerAlbumTracksRequestResult.data }
            .toObservable()
            .flatMapIterable { dataItems -> dataItems }
            .flatMap { dataItem ->
                deezerArtistInfoRepository.requestTrack(dataItem.id).toObservable().zipWith(Observable.just(dataItem),
                    BiFunction { trackResult: DeezerTrackRequestResult, dataItem1: AlbumTracksDataItem ->
                        if (trackResult.resultCode == DeezerTrackRequestResultCode.NETWORK_ERROR) {
                            throw ArtistInfoException(ArtistInfoErrorCode.NO_CONNECTION)
                        }
                        TrackListItem(dataItem1.id, dataItem1.disk_number, dataItem1.title, trackResult.result?.contributors?.map { trackContributor -> trackContributor.name })
                    }
                )
                .subscribeOn(schedulersProvider.io())
            }
            .toList()
            .flatMap { trackListItems ->
                deezerArtistInfoRepository.requestAlbum(albumId).zipWith(Single.just(trackListItems),
                    BiFunction { album: DeezerAlbumRequestResult, trackListItems1: List<TrackListItem> ->
                        Pair(album, trackListItems)
                    }

                )
            }
            .flatMap { albumAndTrackListItemsPair ->
                val album: DeezerAlbumRequestResult = albumAndTrackListItemsPair.first
                val trackListItems: List<TrackListItem> = albumAndTrackListItemsPair.second

                if (album.resultCode == DeezerAlbumRequestResultCode.OK) {
                    if (album.result != null) {
                        Single.just(RequestAlbumTracksResult(RequestAlbumTracksResultCode.OK, albumId, album.result.title, album.result.cover_xl, album.result.contributors.map { it.name }, convertTrackListItemListToVolumeMap(trackListItems)))
                    } else {
                        Single.just(RequestAlbumTracksResult(RequestAlbumTracksResultCode.GENERAL_ERROR, albumId, null, "", null, convertTrackListItemListToVolumeMap(trackListItems)))
                    }
                } else if (album.resultCode == DeezerAlbumRequestResultCode.NETWORK_ERROR) {
                    Single.error(ArtistInfoException(ArtistInfoErrorCode.NO_CONNECTION))
                } else
                    Single.error(ArtistInfoException(ArtistInfoErrorCode.GENERAL_ERROR))
                }
            .onErrorResumeNext { throwable ->
                if (throwable is ArtistInfoException && throwable.errorCode == ArtistInfoErrorCode.NO_CONNECTION) {
                    Single.just(RequestAlbumTracksResult(RequestAlbumTracksResultCode.NO_NETWORK, albumId, null, "", null, null))
                } else
                    Single.just(RequestAlbumTracksResult(RequestAlbumTracksResultCode.GENERAL_ERROR, albumId, null, "", null, null))
            }
            .subscribeOn(schedulersProvider.io())
            .doOnSubscribe { log { i(TAG, "ArtistInfoInteractorImpl.requestAlbumTracks(): Subscribe. albumId = [${albumId}]") } }
            .doOnSuccess { log { i(TAG, "ArtistInfoInteractorImpl.requestAlbumTracks(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "ArtistInfoInteractorImpl.requestAlbumTracks(): Error", it) } }
    }

    private fun convertTrackListItemListToVolumeMap(trackListItems: List<TrackListItem>): Map<Int, TrackList> {
        val volumeMap: MutableMap<Int, TrackList> = HashMap()

        trackListItems.forEach { trackListItem ->

            if (volumeMap.containsKey(trackListItem.diskNumber)) {
                (volumeMap[trackListItem.diskNumber]?.items as MutableList).add(trackListItem)
            } else {
                volumeMap[trackListItem.diskNumber] = TrackList(ArrayList())
                (volumeMap[trackListItem.diskNumber]?.items as MutableList).add(trackListItem)
            }
        }

        return volumeMap
    }

    companion object {
        private const val TAG = "ArtistInfoInteractor"
    }

}