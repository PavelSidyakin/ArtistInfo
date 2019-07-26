package com.artistinfo.model

data class TrackListItem(
    val trackId: Int,
    val diskNumber: Int,
    val trackTitle: String,
    val trackContributors: List<String>?
)