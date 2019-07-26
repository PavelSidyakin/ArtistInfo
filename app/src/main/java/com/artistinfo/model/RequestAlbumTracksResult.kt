package com.artistinfo.model

data class RequestAlbumTracksResult(
    val resultCode: RequestAlbumTracksResultCode,
    val albumId: Int,
    val albumTitle: String?,
    val pictureUrl: String,
    val contributors: List<String>?,
    val trackList: Map<Int, TrackList>? // Volume number to TrackList
)