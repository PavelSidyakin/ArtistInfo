package com.artistinfo.domain.data

import com.artistinfo.model.deezer.DeezerAlbumRequestResult
import com.artistinfo.model.deezer.DeezerAlbumTracksRequestResult
import com.artistinfo.model.deezer.DeezerArtistAlbumsRequestResult
import com.artistinfo.model.deezer.DeezerSearchArtistRequestResult
import com.artistinfo.model.deezer.DeezerTrackRequestResult
import io.reactivex.Single

interface DeezerArtistInfoRepository {

    /**
     *  Performs search request with the given search string and paging parameters
     *
     *  @param name Search string
     *  @param startIndex Start index for paging
     *  @param maxCount Max number of items
     *
     *  Subscribe: io
     *  error: no
     */
    fun searchArtists(name: String, startIndex: Int, maxCount: Int): Single<DeezerSearchArtistRequestResult>

    /**
     *  Requests albums for the artist
     *
     *  @param artistId Artist ID
     *  @param startIndex Start index for paging
     *  @param maxCount Max number of items
     *
     *  Subscribe: io
     *  error: no
     */
    fun requestArtistAlbums(artistId: Int, startIndex: Int, maxCount: Int): Single<DeezerArtistAlbumsRequestResult>

    /**
     *  Requests tracks for the album
     *
     *  @param albumId Album ID
     *  @param startIndex Start index for paging
     *  @param maxCount Max number of items
     *
     *  Subscribe: io
     *  error: no
     */
    fun requestAlbumTracks(albumId: Int, startIndex: Int, maxCount: Int): Single<DeezerAlbumTracksRequestResult>

    /**
     *  Requests the album info
     *  @param albumId Album ID
     *
     *  Subscribe: io
     *  error: no
     */
    fun requestAlbum(albumId: Int): Single<DeezerAlbumRequestResult>

    /**
     *  Requests the track info
     *  @param trackId Track ID
     *
     *  Subscribe: io
     *  error: no
     */
    fun requestTrack(trackId: Int): Single<DeezerTrackRequestResult>

}