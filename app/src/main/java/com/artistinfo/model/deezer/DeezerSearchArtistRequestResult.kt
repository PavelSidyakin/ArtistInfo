package com.artistinfo.model.deezer

import com.artistinfo.model.deezer.rest.search_artist.DeezerSearchArtistResult

data class DeezerSearchArtistRequestResult (
    val resultCode: DeezerSearchArtistRequestResultCode,
    val result: DeezerSearchArtistResult?
)