package com.artistinfo.domain

import com.artistinfo.model.RequestAlbumsResult
import com.artistinfo.model.SearchArtistResult
import com.artistinfo.model.RequestAlbumTracksResult
import io.reactivex.Single

interface ArtistInfoInteractor {

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
    fun searchArtists(name: String, startIndex: Int, maxCount: Int): Single<SearchArtistResult>

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
    fun requestArtistAlbums(artistId: Int, startIndex: Int, maxCount: Int): Single<RequestAlbumsResult>

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
    fun requestAlbumTracks(albumId: Int): Single<RequestAlbumTracksResult>
}