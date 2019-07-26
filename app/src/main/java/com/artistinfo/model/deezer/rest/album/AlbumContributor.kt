package com.artistinfo.model.deezer.rest.album

import com.artistinfo.model.GsonSerializable

data class AlbumContributor (
    val id: Int,
    val name: String

    // TODO: add more fields if needed

) : GsonSerializable