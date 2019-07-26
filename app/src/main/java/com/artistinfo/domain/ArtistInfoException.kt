package com.artistinfo.domain

import java.lang.RuntimeException

class ArtistInfoException(artistInfoErrorCode: ArtistInfoErrorCode) : RuntimeException(artistInfoErrorCode.name) {
    val errorCode = artistInfoErrorCode
}