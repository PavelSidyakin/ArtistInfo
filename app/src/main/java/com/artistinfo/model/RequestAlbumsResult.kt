package com.artistinfo.model

data class RequestAlbumsResult (
    val requestAlbumsResultCode: RequestAlbumsResultCode,
    val albumList: AlbumList?
)