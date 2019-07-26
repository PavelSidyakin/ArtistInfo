package com.artistinfo.model.deezer

import com.artistinfo.model.deezer.rest.album.DeezerAlbumResult

data class DeezerAlbumRequestResult (
    val resultCode: DeezerAlbumRequestResultCode,
    val result: DeezerAlbumResult?
)