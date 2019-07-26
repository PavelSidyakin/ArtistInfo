package com.artistinfo.domain

import com.artistinfo.domain.data.DeezerArtistInfoRepository
import com.artistinfo.model.RequestAlbumTracksResult
import com.artistinfo.model.RequestAlbumTracksResultCode
import com.artistinfo.model.RequestAlbumsResult
import com.artistinfo.model.RequestAlbumsResultCode
import com.artistinfo.model.SearchArtistResult
import com.artistinfo.model.SearchArtistResultCode
import com.artistinfo.model.TrackList
import com.artistinfo.model.TrackListItem
import com.artistinfo.model.deezer.DeezerAlbumRequestResult
import com.artistinfo.model.deezer.DeezerAlbumRequestResultCode
import com.artistinfo.model.deezer.DeezerAlbumTracksRequestResult
import com.artistinfo.model.deezer.DeezerAlbumTracksRequestResultCode
import com.artistinfo.model.deezer.DeezerArtistAlbumsRequestResult
import com.artistinfo.model.deezer.DeezerArtistAlbumsRequestResultCode
import com.artistinfo.model.deezer.DeezerSearchArtistRequestResult
import com.artistinfo.model.deezer.DeezerSearchArtistRequestResultCode
import com.artistinfo.model.deezer.DeezerTrackRequestResult
import com.artistinfo.model.deezer.DeezerTrackRequestResultCode
import com.artistinfo.model.deezer.rest.album.AlbumContributor
import com.artistinfo.model.deezer.rest.album.DeezerAlbumResult
import com.artistinfo.model.deezer.rest.album_tracks.AlbumTracksDataItem
import com.artistinfo.model.deezer.rest.album_tracks.DeezerAlbumTracksResult
import com.artistinfo.model.deezer.rest.track.DeezerTrackResult
import com.artistinfo.model.deezer.rest.track.TrackContributor
import com.artistinfo.utils.NetworkUtils
import com.artistinfo.utils.logs.XLog
import com.artistinfo.utils.rx.SchedulersProvider
import com.artistinfo.utils.rx.SchedulersProviderStub
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@DisplayName("ArtistInfoInteractorImpl tests")
class ArtistInfoInteractorImplTest {

    @Mock
    private lateinit var deezerArtistInfoRepository: DeezerArtistInfoRepository
    @Mock
    private lateinit var networkUtils: NetworkUtils

    private val schedulersProvider: SchedulersProvider = SchedulersProviderStub()

    private lateinit var artistInfoInteractorImpl: ArtistInfoInteractorImpl

    @BeforeEach
    fun beforeEachTest() {
        XLog.enableLogging(false)

        MockitoAnnotations.initMocks(this)

        artistInfoInteractorImpl = ArtistInfoInteractorImpl(deezerArtistInfoRepository, networkUtils, schedulersProvider)

    }

    @Nested
    @DisplayName("When no internet")
    inner class NoInternet {
        @BeforeEach
        fun beforeEachTest() {
            `when`(networkUtils.networkConnectionOn).thenReturn(false)
        }

        @Test
        @DisplayName("searchArtists() should emit error NO_CONNECTION")
        fun searchArtistsTest() {
            `when`(deezerArtistInfoRepository.searchArtists(anyString(), anyInt(), anyInt())).thenReturn(Single.just(DeezerSearchArtistRequestResult(DeezerSearchArtistRequestResultCode.GENERAL_ERROR, null)))

            artistInfoInteractorImpl.searchArtists(ARTIST_NAME, START_INDEX, MAX_COUNT)
                .test()
                .await()
                .assertComplete()
                .assertResult(SearchArtistResult(SearchArtistResultCode.NO_NETWORK, null))

            verify(deezerArtistInfoRepository, never()).searchArtists(anyString(), anyInt(), anyInt())
        }

        @Test
        @DisplayName("requestArtistAlbums() should emit error NO_CONNECTION")
        fun requestArtistAlbumsTest() {
            `when`(deezerArtistInfoRepository.requestArtistAlbums(anyInt(), anyInt(), anyInt())).thenReturn(Single.just(DeezerArtistAlbumsRequestResult(DeezerArtistAlbumsRequestResultCode.GENERAL_ERROR, null)))

            artistInfoInteractorImpl.requestArtistAlbums(ARTIST_ID, START_INDEX, MAX_COUNT)
                .test()
                .await()
                .assertComplete()
                .assertResult(RequestAlbumsResult(RequestAlbumsResultCode.NO_NETWORK, null))

            verify(deezerArtistInfoRepository, never()).requestArtistAlbums(anyInt(), anyInt(), anyInt())
        }

        @Test
        @DisplayName("requestAlbumTracks() should emit error NO_CONNECTION")
        fun requestAlbumTracksTest() {
            `when`(deezerArtistInfoRepository.requestAlbumTracks(anyInt(), anyInt(), anyInt())).thenReturn(Single.just(DeezerAlbumTracksRequestResult(DeezerAlbumTracksRequestResultCode.GENERAL_ERROR, null)))

            artistInfoInteractorImpl.requestAlbumTracks(ALBUM_ID)
                .test()
                .await()
                .assertComplete()
                .assertResult(RequestAlbumTracksResult(RequestAlbumTracksResultCode.NO_NETWORK, ALBUM_ID, null, "", null, null))

            verify(deezerArtistInfoRepository, never()).requestAlbumTracks(anyInt(), anyInt(), anyInt())
        }
    }


    @Nested
    @DisplayName("When connection OK")
    inner class HasInternet {
        @BeforeEach
        fun beforeEachTest() {
            `when`(networkUtils.networkConnectionOn).thenReturn(true)
        }


        @Test
        @DisplayName("when repo's searchArtists() returns GENERAL_ERROR searchArtists() should emit GENERAL_ERROR")
        fun searchArtistsTest1() {
            `when`(deezerArtistInfoRepository.searchArtists(anyString(), anyInt(), anyInt())).thenReturn(Single.just(DeezerSearchArtistRequestResult(DeezerSearchArtistRequestResultCode.GENERAL_ERROR, null)))

            artistInfoInteractorImpl.searchArtists(ARTIST_NAME, START_INDEX, MAX_COUNT)
                .test()
                .await()
                .assertComplete()
                .assertResult(SearchArtistResult(SearchArtistResultCode.GENERAL_ERROR, null))

            verify(deezerArtistInfoRepository).searchArtists(anyString(), anyInt(), anyInt())
        }

        @Test
        @DisplayName("when repo's searchArtists() returns NETWORK_ERROR searchArtists() should emit NO_NETWORK")
        fun searchArtistsTest2() {
            `when`(deezerArtistInfoRepository.searchArtists(anyString(), anyInt(), anyInt())).thenReturn(Single.just(DeezerSearchArtistRequestResult(DeezerSearchArtistRequestResultCode.NETWORK_ERROR, null)))

            artistInfoInteractorImpl.searchArtists(ARTIST_NAME, START_INDEX, MAX_COUNT)
                .test()
                .await()
                .assertComplete()
                .assertResult(SearchArtistResult(SearchArtistResultCode.NO_NETWORK, null))

            verify(deezerArtistInfoRepository).searchArtists(anyString(), anyInt(), anyInt())
        }

        @Test
        @DisplayName("when repo's requestArtistAlbums() returns GENERAL_ERROR requestArtistAlbums() should emit GENERAL_ERROR")
        fun requestArtistAlbumsTest1() {
            `when`(deezerArtistInfoRepository.requestArtistAlbums(anyInt(), anyInt(), anyInt())).thenReturn(Single.just(DeezerArtistAlbumsRequestResult(DeezerArtistAlbumsRequestResultCode.GENERAL_ERROR, null)))

            artistInfoInteractorImpl.requestArtistAlbums(ARTIST_ID, START_INDEX, MAX_COUNT)
                .test()
                .await()
                .assertComplete()
                .assertResult(RequestAlbumsResult(RequestAlbumsResultCode.GENERAL_ERROR, null))

            verify(deezerArtistInfoRepository).requestArtistAlbums(anyInt(), anyInt(), anyInt())
        }

        @Test
        @DisplayName("when repo's requestArtistAlbums() returns NETWORK_ERROR requestArtistAlbums() should emit NO_NETWORK")
        fun requestArtistAlbumsTest2() {
            `when`(deezerArtistInfoRepository.requestArtistAlbums(anyInt(), anyInt(), anyInt())).thenReturn(Single.just(DeezerArtistAlbumsRequestResult(DeezerArtistAlbumsRequestResultCode.NETWORK_ERROR, null)))

            artistInfoInteractorImpl.requestArtistAlbums(ARTIST_ID, START_INDEX, MAX_COUNT)
                .test()
                .await()
                .assertComplete()
                .assertResult(RequestAlbumsResult(RequestAlbumsResultCode.NO_NETWORK, null))

            verify(deezerArtistInfoRepository).requestArtistAlbums(anyInt(), anyInt(), anyInt())
        }

        @Test
        @DisplayName("when repo's requestAlbumTracks() returns GENERAL_ERROR requestAlbumTracks() should emit GENERAL_ERROR")
        fun requestAlbumTracksTest1() {
            `when`(deezerArtistInfoRepository.requestAlbumTracks(anyInt(), anyInt(), anyInt())).thenReturn(Single.just(DeezerAlbumTracksRequestResult(DeezerAlbumTracksRequestResultCode.GENERAL_ERROR, null)))

            artistInfoInteractorImpl.requestAlbumTracks(ALBUM_ID)
                .test()
                .await()
                .assertComplete()
                .assertResult(RequestAlbumTracksResult(RequestAlbumTracksResultCode.GENERAL_ERROR, ALBUM_ID, null, "", null, null))

            verify(deezerArtistInfoRepository).requestAlbumTracks(anyInt(), anyInt(), anyInt())
        }

        @Test
        @DisplayName("when repo's requestAlbumTracks() returns NETWORK_ERROR requestAlbumTracks() should emit NO_NETWORK")
        fun requestAlbumTracksTest2() {
            `when`(deezerArtistInfoRepository.requestAlbumTracks(anyInt(), anyInt(), anyInt())).thenReturn(Single.just(DeezerAlbumTracksRequestResult(DeezerAlbumTracksRequestResultCode.NETWORK_ERROR, null)))

            artistInfoInteractorImpl.requestAlbumTracks(ALBUM_ID)
                .test()
                .await()
                .assertComplete()
                .assertResult(RequestAlbumTracksResult(RequestAlbumTracksResultCode.NO_NETWORK, ALBUM_ID, null, "", null, null))

            verify(deezerArtistInfoRepository).requestAlbumTracks(anyInt(), anyInt(), anyInt())
        }

        @Test
        @DisplayName("when repo's requestAlbumTracks() returns OK, but requestTrack() returns NETWORK_ERROR. requestAlbumTracks() should emit NO_NETWORK")
        fun requestAlbumTracksTest3() {
            `when`(deezerArtistInfoRepository.requestTrack(anyInt())).thenReturn(Single.just(DeezerTrackRequestResult(DeezerTrackRequestResultCode.NETWORK_ERROR, null)))
            `when`(deezerArtistInfoRepository.requestAlbumTracks(anyInt(), anyInt(), anyInt())).thenReturn(
                Single.just(
                    DeezerAlbumTracksRequestResult(DeezerAlbumTracksRequestResultCode.OK, DeezerAlbumTracksResult(
                        listOf(AlbumTracksDataItem(1, "t1", 1), AlbumTracksDataItem(2, "t2", 1)),
                        2, ""
                    )
                    )
                )
            )

            artistInfoInteractorImpl.requestAlbumTracks(ALBUM_ID)
                .test()
                .await()
                .assertComplete()
                .assertResult(RequestAlbumTracksResult(RequestAlbumTracksResultCode.NO_NETWORK, ALBUM_ID, null, "", null, null))

            verify(deezerArtistInfoRepository).requestAlbumTracks(anyInt(), anyInt(), anyInt())
        }

        @Test
        @DisplayName("when repo's requestAlbumTracks() returns OK, but requestTrack() returns OK, but requestAlbum() returns NETWORK_ERROR. requestAlbumTracks() should emit NO_NETWORK")
        fun requestAlbumTracksTest4() {
            `when`(deezerArtistInfoRepository.requestAlbum(anyInt())).thenReturn(Single.just(DeezerAlbumRequestResult(DeezerAlbumRequestResultCode.NETWORK_ERROR, null)))

            `when`(deezerArtistInfoRepository.requestTrack(anyInt())).thenReturn(Single.just(DeezerTrackRequestResult(DeezerTrackRequestResultCode.OK,
                DeezerTrackResult(1, "ttt", listOf(TrackContributor(11, "nnnn"))))))
            `when`(deezerArtistInfoRepository.requestAlbumTracks(anyInt(), anyInt(), anyInt())).thenReturn(
                Single.just(
                    DeezerAlbumTracksRequestResult(DeezerAlbumTracksRequestResultCode.OK, DeezerAlbumTracksResult(
                        listOf(AlbumTracksDataItem(1, "t1", 1), AlbumTracksDataItem(2, "t2", 1)),
                        2, "")
                    )
                )
            )

            artistInfoInteractorImpl.requestAlbumTracks(ALBUM_ID)
                .test()
                .await()
                .assertComplete()
                .assertResult(RequestAlbumTracksResult(RequestAlbumTracksResultCode.NO_NETWORK, ALBUM_ID, null, "", null, null))

            verify(deezerArtistInfoRepository).requestAlbumTracks(anyInt(), anyInt(), anyInt())
        }


        @Test
        @DisplayName("when repo's requestAlbumTracks() returns OK, but requestTrack() returns OK, and requestAlbum() returns OK. requestAlbumTracks() should emit OK")
        fun requestAlbumTracksTest5() {
            val albumId = 457783545
            val albumContributorId = 1222
            val albumContributorName = "albumContributorName"
            val albumTitle = "albumTitle"
            val coverMedium = "coverMedium"
            val coverBig = "coverBig"
            val coverXl = "coverXl"

            val track1Id = 445631
            val track1Title = "track1Title"
            val track1ContributorId = 431
            val track1ContributorName = "track1ContributorName"
            val track1DiskNumber = 1

            val track2Id = 43534
            val track2Title = "track2Title"
            val track2ContributorId = 88431
            val track2ContributorName = "track2ContributorName"
            val track2DiskNumber = 2

            `when`(deezerArtistInfoRepository.requestAlbum(albumId)).thenReturn(
                Single.just(
                    DeezerAlbumRequestResult(DeezerAlbumRequestResultCode.OK, DeezerAlbumResult(albumId, albumTitle, coverMedium, coverBig, coverXl, listOf(AlbumContributor(albumContributorId, albumContributorName))))))

            `when`(deezerArtistInfoRepository.requestTrack(anyInt()))
                .thenReturn(
                    Single.just(DeezerTrackRequestResult(DeezerTrackRequestResultCode.OK,
                        DeezerTrackResult(track1Id, track1Title, listOf(TrackContributor(track1ContributorId, track1ContributorName))))))
                .thenReturn(
                    Single.just(DeezerTrackRequestResult(DeezerTrackRequestResultCode.OK,
                        DeezerTrackResult(track2Id, track2Title, listOf(TrackContributor(track2ContributorId, track2ContributorName))))))

            `when`(deezerArtistInfoRepository.requestAlbumTracks(albumId, 0, Int.MAX_VALUE)).thenReturn(
                Single.just(
                    DeezerAlbumTracksRequestResult(DeezerAlbumTracksRequestResultCode.OK, DeezerAlbumTracksResult(
                        listOf(AlbumTracksDataItem(track1Id, track1Title, track1DiskNumber), AlbumTracksDataItem(track2Id, track2Title, track2DiskNumber)),
                        2, "")
                    )
                )
            )

            val result = RequestAlbumTracksResult(
                RequestAlbumTracksResultCode.OK,
                albumId,
                albumTitle,
                coverXl,
                listOf(albumContributorName),
                    mapOf(
                        Pair(track2DiskNumber,
                            TrackList(listOf(
                                    TrackListItem(track2Id, track2DiskNumber, track2Title, listOf(track2ContributorName))
                                )
                            )
                        ),
                        Pair(track1DiskNumber,
                            TrackList(listOf(
                                    TrackListItem(track1Id, track1DiskNumber, track1Title, listOf(track1ContributorName))
                                )
                            )
                        )
                    )
                )

            artistInfoInteractorImpl.requestAlbumTracks(albumId)
                .test()
                .await()
                .assertComplete()
                .assertResult(result)

            verify(deezerArtistInfoRepository).requestAlbumTracks(anyInt(), anyInt(), anyInt())
            verify(deezerArtistInfoRepository, times(2)).requestTrack(anyInt())
            verify(deezerArtistInfoRepository).requestAlbum(anyInt())
        }

    }

    private companion object {
        private const val ARTIST_NAME = "dsckewjdijwei"
        private const val ARTIST_ID = 111
        private const val ALBUM_ID = 222
        private const val START_INDEX = 11
        private const val MAX_COUNT = 22


    }
}