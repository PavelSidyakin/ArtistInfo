package com.artistinfo.model.deezer.rest.artist_albums

import com.artistinfo.model.GsonSerializable

data class ArtistAlbumsDataItem (
    val id: Int,
    val title: String,
    val cover_medium: String,
    val cover_big: String

    // TODO: add more fields if needed

) : GsonSerializable