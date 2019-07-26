package com.artistinfo.model.deezer.rest.album_tracks

import com.artistinfo.model.GsonSerializable

data class DeezerAlbumTracksResult (
    val data: List<AlbumTracksDataItem>?,
    val total: Int,
    val next: String?
) : GsonSerializable