package com.artistinfo.model.deezer.rest.album

import com.artistinfo.model.GsonSerializable

data class DeezerAlbumResult (
    val id: Int,
    val title: String,
    val cover_medium: String,
    val cover_big: String,
    val cover_xl: String,

    val contributors: List<AlbumContributor>

    // TODO: add more fields if needed

) : GsonSerializable