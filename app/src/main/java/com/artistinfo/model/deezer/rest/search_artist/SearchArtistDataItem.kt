package com.artistinfo.model.deezer.rest.search_artist

import com.artistinfo.model.GsonSerializable

data class SearchArtistDataItem (
    val id: Int,
    val name: String,
    val picture_small: String,
    val picture_medium: String

    // TODO: add more fields if needed

) : GsonSerializable
