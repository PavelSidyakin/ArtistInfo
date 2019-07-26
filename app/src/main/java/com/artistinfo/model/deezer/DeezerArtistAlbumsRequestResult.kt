package com.artistinfo.model.deezer

import com.artistinfo.model.deezer.rest.artist_albums.DeezerArtistAlbumsResult

data class DeezerArtistAlbumsRequestResult (
    val resultCode: DeezerArtistAlbumsRequestResultCode,
    val result: DeezerArtistAlbumsResult?

)