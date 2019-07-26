package com.artistinfo.presentation.view.album.recycler

data class AlbumItem(
    val itemType: ItemType,
    val volumeTitle: String?,
    val trackTitle: String?,
    val artistTitle: String?,
    val number: Int?
)