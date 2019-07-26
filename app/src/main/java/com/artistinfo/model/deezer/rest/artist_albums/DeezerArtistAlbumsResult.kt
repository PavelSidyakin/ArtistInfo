package com.artistinfo.model.deezer.rest.artist_albums

import com.artistinfo.model.GsonSerializable

data class DeezerArtistAlbumsResult(
    val data: List<ArtistAlbumsDataItem>?,
    val total: Int,
    val next: String?
) : GsonSerializable