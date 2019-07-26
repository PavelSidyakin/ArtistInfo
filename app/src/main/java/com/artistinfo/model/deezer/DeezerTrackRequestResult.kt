package com.artistinfo.model.deezer

import com.artistinfo.model.deezer.rest.track.DeezerTrackResult

data class DeezerTrackRequestResult (
    val resultCode: DeezerTrackRequestResultCode,
    val result: DeezerTrackResult?
)