package com.artistinfo.model.deezer.rest.search_artist

import com.artistinfo.model.GsonSerializable

data class DeezerSearchArtistResult(
    val data: List<SearchArtistDataItem>?,
    val total: Int,
    val next: String?
) : GsonSerializable
