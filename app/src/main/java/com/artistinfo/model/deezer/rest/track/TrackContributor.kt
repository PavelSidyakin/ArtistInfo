package com.artistinfo.model.deezer.rest.track

import com.artistinfo.model.GsonSerializable

data class TrackContributor (
    val id: Int,
    val name: String

    // TODO: add more fields if needed

) : GsonSerializable