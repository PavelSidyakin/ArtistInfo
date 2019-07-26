package com.artistinfo.model


data class AlbumListItem(
    val id: Int,
    val albumTitle: String,
    val contributors: List<String>?,
    val pictureUrl: String
)