package com.artistinfo.presentation.presenter.search

import com.artistinfo.domain.ArtistInfoInteractor
import com.artistinfo.model.ArtistList
import com.artistinfo.model.ArtistListItem
import com.artistinfo.model.SearchArtistResultCode
import com.artistinfo.model.SearchArtistResult
import com.artistinfo.presentation.view.search.ArtistSearchView
import com.artistinfo.utils.logs.XLog
import com.artistinfo.utils.rx.SchedulersProviderStub
import io.reactivex.Single
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.InOrder
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@DisplayName("SearchArtistPresenter tests")
internal class SearchArtistPresenterTest {

    lateinit var searchArtistPresenter: SearchArtistPresenter

    @Mock
    private lateinit var artistInfoInteractor: ArtistInfoInteractor

    @Mock
    private lateinit var artistSearchView: ArtistSearchView

    private val lastEnteredText = "----------------- lastEnteredText ---------------- "


    @BeforeEach
    fun beforeEachTest() {
        XLog.enableLogging(false)
        MockitoAnnotations.initMocks(this)
        setupPresenter()

        searchArtistPresenter.performSearchTimeoutMillis = 0
    }

    @Nested
    @DisplayName("When searchArtists() successful")
    inner class SuccessfulSearchRequest {
        @BeforeEach
        fun beforeEachTest() {
            // when
            `when`(artistInfoInteractor.searchArtists(anyString(), anyInt(), anyInt()))
                .thenReturn(Single.just(SearchArtistResult(SearchArtistResultCode.OK,
                    ArtistList(listOf(
                        ArtistListItem(1, "111", "url"),
                        ArtistListItem(2, "222", "url")
                    )))))
        }

        @Nested
        @DisplayName("Should be only one request with the last entered text")
        inner class ShouldBeOnlyOneRequestTests {

            @Test
            @DisplayName("when typing text and then submit")
            fun shouldBeOnlyOneRequest1() {

                // action
                searchArtistPresenter.onSearchTextChanged("aaaaaa")
                searchArtistPresenter.onSearchTextSubmitted(lastEnteredText)

                searchArtistPresenter.requestCompletedCompletable
                    .test()
                    .await()
                    .assertComplete()
            }

            @Test
            @DisplayName("when typing one text, then another")
            fun shouldBeOnlyOneRequest2() {

                // action
                searchArtistPresenter.onSearchTextChanged("aaaa")
                searchArtistPresenter.onSearchTextChanged("bbbb")
                searchArtistPresenter.onSearchTextChanged("cccc")
                searchArtistPresenter.onSearchTextChanged(lastEnteredText)

                searchArtistPresenter.requestCompletedCompletable
                    .test()
                    .await()
                    .assertComplete()
            }

            @Test
            @DisplayName("when submit text")
            fun shouldBeOnlyOneRequest3() {

                // action
                searchArtistPresenter.onSearchTextSubmitted(lastEnteredText)

                searchArtistPresenter.requestCompletedCompletable
                    .test()
                    .await()
                    .assertComplete()
            }

            @AfterEach
            fun afterEachTest() {

                // verify
                verify(artistSearchView, never()).showGeneralError(true)
                val inOrder: InOrder = Mockito.inOrder(artistSearchView)
                inOrder.verify(artistSearchView).showProgress(true)
                inOrder.verify(artistSearchView).showProgress(false)
                verify(artistInfoInteractor, times(1)).searchArtists(lastEnteredText, 0, SearchArtistPresenter.INITIAL_PAGE_SIZE)
            }
        }
    }

    @Nested
    @DisplayName("When searchArtists() failed and then successful, should show error")
    inner class FailedSearchRequest {
        @BeforeEach
        fun beforeEachTest() {
            // when
            `when`(artistInfoInteractor.searchArtists(anyString(), anyInt(), anyInt()))
                .thenReturn(Single.just(SearchArtistResult(SearchArtistResultCode.GENERAL_ERROR, null)))
                .thenReturn(Single.just(SearchArtistResult(SearchArtistResultCode.OK,
                    ArtistList(listOf(
                        ArtistListItem(1, "111", "url"),
                        ArtistListItem(2, "222", "url")
                    )))))
        }

        @Test
        @DisplayName("searchArtists() shouldn't be called one more time")
        fun showError() {

            // action
            searchArtistPresenter.onSearchTextSubmitted(lastEnteredText)

            searchArtistPresenter.requestCompletedCompletable
                .test()
                .await()
                .assertComplete()

            // verify
            verify(artistInfoInteractor, times(1)).searchArtists(lastEnteredText, 0, SearchArtistPresenter.INITIAL_PAGE_SIZE)
        }

        @Test
        @DisplayName("click on error message should retry request")
        fun clickOnError() {

            // action
            searchArtistPresenter.onSearchTextSubmitted(lastEnteredText)
            searchArtistPresenter.onErrorClicked()

            searchArtistPresenter.requestCompletedCompletable
                .test()
                .await()
                .assertComplete()

            // verify
            verify(artistInfoInteractor, times(2)).searchArtists(lastEnteredText, 0, SearchArtistPresenter.INITIAL_PAGE_SIZE)
        }

        @AfterEach
        fun afterEachTest() {
            verify(artistSearchView).showGeneralError(true)
            verify(artistSearchView, never()).showNoNetworkError(true)
            val inOrder: InOrder = Mockito.inOrder(artistSearchView)
            inOrder.verify(artistSearchView).showProgress(true)
            inOrder.verify(artistSearchView).showProgress(false)
        }

    }

    @Test
    @DisplayName("When no network error, should show network error message")
    fun clickOnError() {
        // when
        `when`(artistInfoInteractor.searchArtists(anyString(), anyInt(), anyInt()))
            .thenReturn(Single.just(SearchArtistResult(SearchArtistResultCode.NO_NETWORK, null)))

        // action
        searchArtistPresenter.onSearchTextSubmitted(lastEnteredText)

        searchArtistPresenter.requestCompletedCompletable
            .test()
            .await()
            .assertComplete()

        // verify
        verify(artistSearchView, never()).showGeneralError(true)
        verify(artistSearchView).showNoNetworkError(true)
        verify(artistInfoInteractor, times(1)).searchArtists(lastEnteredText, 0, SearchArtistPresenter.INITIAL_PAGE_SIZE)
    }


    private fun setupPresenter() {
        searchArtistPresenter = SearchArtistPresenter(SchedulersProviderStub(), artistInfoInteractor)
        searchArtistPresenter.attachView(artistSearchView)
    }


}