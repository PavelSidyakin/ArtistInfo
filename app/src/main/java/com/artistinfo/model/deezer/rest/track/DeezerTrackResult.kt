package com.artistinfo.model.deezer.rest.track

import com.artistinfo.model.GsonSerializable

data class DeezerTrackResult (
    val id: Int,
    val title: String,


    val contributors: List<TrackContributor>

    // TODO: add more fields if needed

) : GsonSerializable