package com.artistinfo.presentation.view.search

import android.arch.paging.PagedList
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.artistinfo.model.ArtistListItem

interface ArtistSearchView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun updateDisplayData(data: PagedList<ArtistListItem>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun clearList()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showGeneralError(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showNoNetworkError(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showProgress(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openArtistAlbums(artistId: Int)

}