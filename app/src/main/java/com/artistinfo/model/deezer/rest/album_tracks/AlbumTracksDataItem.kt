package com.artistinfo.model.deezer.rest.album_tracks

import com.artistinfo.model.GsonSerializable

data class AlbumTracksDataItem (
    val id: Int,
    val title: String,
    val disk_number: Int

    // TODO: add more fields if needed

) : GsonSerializable