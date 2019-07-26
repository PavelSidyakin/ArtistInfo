package com.artistinfo.presentation.view.album

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.artistinfo.model.TrackList

interface AlbumDetailsView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showGeneralError(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showNoNetworkError(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showProgress(show: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setAlbumTitle(albumName: String)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setAlbumPicture(pictureUrl: String)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setAlbumContributors(contributors: List<String>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun setTracksData(items: Map<Int, TrackList>)


}