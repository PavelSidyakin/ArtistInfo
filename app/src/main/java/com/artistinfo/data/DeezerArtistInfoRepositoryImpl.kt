package com.artistinfo.data

import com.artistinfo.domain.data.DeezerArtistInfoRepository
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
import com.artistinfo.model.deezer.rest.album.DeezerAlbumResult
import com.artistinfo.model.deezer.rest.album_tracks.DeezerAlbumTracksResult
import com.artistinfo.model.deezer.rest.search_artist.DeezerSearchArtistResult
import com.artistinfo.model.deezer.rest.track.DeezerTrackResult
import com.artistinfo.utils.logs.log
import com.artistinfo.utils.rx.SchedulersProvider
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.RuntimeException
import javax.inject.Inject

class DeezerArtistInfoRepositoryImpl @Inject constructor(
    private val schedulersProvider: SchedulersProvider
) : DeezerArtistInfoRepository {

    private val deezerRetrofit: Retrofit by lazy { createRetrofit() }

    override fun searchArtists(name: String, startIndex: Int, maxCount: Int): Single<DeezerSearchArtistRequestResult> {
        return Single.fromCallable { createSearchArtistsService() }
            .flatMap { service -> service.searchArtists(name, startIndex, maxCount) }
            .map { deezerSearchArtistResult -> if (deezerSearchArtistResult.data == null) throw RuntimeException("Empty result"); deezerSearchArtistResult }
            .map { deezerSearchArtistResult -> DeezerSearchArtistRequestResult(DeezerSearchArtistRequestResultCode.OK, deezerSearchArtistResult) }
            .doOnSubscribe { log { i(TAG, "DeezerArtistInfoRepositoryImpl.searchArtists(): Subscribe. name = [${name}], startIndex = [${startIndex}], maxCount = [${maxCount}]") } }
            .doOnSuccess { log { i(TAG, "DeezerArtistInfoRepositoryImpl.searchArtists(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "DeezerArtistInfoRepositoryImpl.searchArtists(): Error", it) } }
            .onErrorResumeNext { Single.just(DeezerSearchArtistRequestResult(DeezerSearchArtistRequestResultCode.GENERAL_ERROR, null)) }
            .subscribeOn(schedulersProvider.io())
    }

    override fun requestArtistAlbums(artistId: Int, startIndex: Int, maxCount: Int): Single<DeezerArtistAlbumsRequestResult> {
        return Single.fromCallable { createRequestArtistAlbumsService() }
            .flatMap { service -> service.requestArtistAlbums(artistId, startIndex, maxCount) }
            .map { deezerArtistAlbumsResult -> if (deezerArtistAlbumsResult.data == null) throw RuntimeException("Empty result"); deezerArtistAlbumsResult }
            .map { deezerArtistAlbumsResult -> DeezerArtistAlbumsRequestResult(DeezerArtistAlbumsRequestResultCode.OK, deezerArtistAlbumsResult) }
            .doOnSubscribe { log { i(TAG, "DeezerArtistInfoRepositoryImpl.requestArtistAlbums(): Subscribe. artistId = [${artistId}], startIndex = [${startIndex}], maxCount = [${maxCount}]") } }
            .doOnSuccess { log { i(TAG, "DeezerArtistInfoRepositoryImpl.requestArtistAlbums(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "DeezerArtistInfoRepositoryImpl.requestArtistAlbums(): Error", it) } }
            .onErrorResumeNext { Single.just(DeezerArtistAlbumsRequestResult(DeezerArtistAlbumsRequestResultCode.GENERAL_ERROR, null)) }
            .subscribeOn(schedulersProvider.io())
    }

    override fun requestAlbumTracks(albumId: Int, startIndex: Int, maxCount: Int): Single<DeezerAlbumTracksRequestResult> {
        return Single.fromCallable { createRequestAlbumTracksService() }
            .flatMap { service -> service.requestAlbumTracks(albumId, startIndex, maxCount) }
            .map { deezerAlbumTracksResult -> if (deezerAlbumTracksResult.data == null) throw RuntimeException("Empty result"); deezerAlbumTracksResult }
            .map { deezerAlbumTracksResult -> DeezerAlbumTracksRequestResult(DeezerAlbumTracksRequestResultCode.OK, deezerAlbumTracksResult) }
            .doOnSubscribe { log { i(TAG, "DeezerArtistInfoRepositoryImpl.requestAlbumTracks(): Subscribe. albumId = [${albumId}], startIndex = [${startIndex}], maxCount = [${maxCount}]") } }
            .doOnSuccess { log { i(TAG, "DeezerArtistInfoRepositoryImpl.requestAlbumTracks(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "DeezerArtistInfoRepositoryImpl.requestAlbumTracks(): Error", it) } }
            .onErrorResumeNext { Single.just(DeezerAlbumTracksRequestResult(DeezerAlbumTracksRequestResultCode.GENERAL_ERROR, null)) }
            .subscribeOn(schedulersProvider.io())
    }

    override fun requestAlbum(albumId: Int): Single<DeezerAlbumRequestResult> {
        return Single.fromCallable { createRequestAlbumService() }
            .flatMap { service -> service.requestAlbum(albumId) }
            .map { deezerAlbumResult -> DeezerAlbumRequestResult(DeezerAlbumRequestResultCode.OK, deezerAlbumResult) }
            .doOnSubscribe { log { i(TAG, "DeezerArtistInfoRepositoryImpl.requestAlbum(): Subscribe. albumId = [${albumId}]") } }
            .doOnSuccess { log { i(TAG, "DeezerArtistInfoRepositoryImpl.requestAlbum(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "DeezerArtistInfoRepositoryImpl.requestAlbum(): Error", it) } }
            .onErrorResumeNext { Single.just(DeezerAlbumRequestResult(DeezerAlbumRequestResultCode.GENERAL_ERROR, null)) }
            .subscribeOn(schedulersProvider.io())
    }

    override fun requestTrack(trackId: Int): Single<DeezerTrackRequestResult> {
        return Single.fromCallable { createRequestTrackService() }
            .flatMap { service -> service.requestTrack(trackId) }
            .map { deezerTrackResult -> DeezerTrackRequestResult(DeezerTrackRequestResultCode.OK, deezerTrackResult) }
            .doOnSubscribe { log { i(TAG, "DeezerArtistInfoRepositoryImpl.requestTrack(): Subscribe. trackId = [${trackId}]") } }
            .doOnSuccess { log { i(TAG, "DeezerArtistInfoRepositoryImpl.requestTrack(): Success. Result: $it") } }
            .doOnError { log { w(TAG, "DeezerArtistInfoRepositoryImpl.requestTrack(): Error", it) } }
            .onErrorResumeNext { Single.just(DeezerTrackRequestResult(DeezerTrackRequestResultCode.GENERAL_ERROR, null)) }
            .subscribeOn(schedulersProvider.io())
    }

    private fun createSearchArtistsService(): SearchArtistsService {
        return deezerRetrofit.create(SearchArtistsService::class.java);
    }

    private fun createRequestArtistAlbumsService(): RequestArtistAlbumsService {
        return deezerRetrofit.create(RequestArtistAlbumsService::class.java);
    }

    private fun createRequestAlbumService(): RequestAlbumService {
        return deezerRetrofit.create(RequestAlbumService::class.java);
    }

    private fun createRequestAlbumTracksService(): RequestAlbumTracksService {
        return deezerRetrofit.create(RequestAlbumTracksService::class.java);
    }

    private fun createRequestTrackService(): RequestTrackService {
        return deezerRetrofit.create(RequestTrackService::class.java);
    }

    private interface SearchArtistsService {
        @GET("search/artist/")
        fun searchArtists(@Query("q") name: String,
                          @Query("index") index: Int,
                          @Query("limit") maxCount: Int): Single<DeezerSearchArtistResult>
    }

    private interface RequestArtistAlbumsService {
        @GET("artist/{artistId}/albums")
        fun requestArtistAlbums(@Path("artistId") artistId: Int,
                          @Query("index") index: Int,
                          @Query("limit") maxCount: Int): Single<com.artistinfo.model.deezer.rest.artist_albums.DeezerArtistAlbumsResult>
    }

    private interface RequestAlbumService {
        @GET("album/{albumId}")
        fun requestAlbum(@Path("albumId") albumId: Int): Single<DeezerAlbumResult>
    }

    private interface RequestAlbumTracksService {
        @GET("album/{albumId}/tracks")
        fun requestAlbumTracks(@Path("albumId") albumId: Int,
                          @Query("index") index: Int,
                          @Query("limit") maxCount: Int): Single<DeezerAlbumTracksResult>
    }

    private interface RequestTrackService {
        @GET("track/{trackId}")
        fun requestTrack(@Path("trackId") trackId: Int): Single<DeezerTrackResult>
    }

    private fun createRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor() {message ->
            log { i(TAG, message) }
        }
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("http://api.deezer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build();
    }

    companion object {
        private const val TAG = "DeezerArtistInfoRepo"
    }

}