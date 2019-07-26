package com.artistinfo.model.deezer

import com.artistinfo.model.deezer.rest.album_tracks.DeezerAlbumTracksResult

data class DeezerAlbumTracksRequestResult (
    val resultCode: DeezerAlbumTracksRequestResultCode,
    val result: DeezerAlbumTracksResult?
)
