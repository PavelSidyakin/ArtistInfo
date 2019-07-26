package com.artistinfo.presentation.view.album

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.artistinfo.R
import com.artistinfo.TheApplication
import com.artistinfo.domain.ArtistInfoInteractor
import com.artistinfo.domain.data.ApplicationProvider
import com.artistinfo.model.TrackList
import com.artistinfo.presentation.presenter.album.AlbumDetailsPresenter
import com.artistinfo.presentation.view.album.recycler.AlbumDetailsAdapter
import com.artistinfo.presentation.view.album.recycler.AlbumItem
import com.artistinfo.presentation.view.album.recycler.ItemType
import com.artistinfo.utils.rx.SchedulersProvider
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_album_details.iv_album_details_cover
import kotlinx.android.synthetic.main.layout_album_details.pb_album_details
import kotlinx.android.synthetic.main.layout_album_details.rv_album_details_tracks
import kotlinx.android.synthetic.main.layout_album_details.toolbar_album_details
import kotlinx.android.synthetic.main.layout_album_details.tv_album_details_artist
import kotlinx.android.synthetic.main.layout_album_details.tv_album_details_error
import kotlinx.android.synthetic.main.layout_album_details.tv_album_details_title
import javax.inject.Inject

class AlbumDetailsFragment : MvpAppCompatFragment(), AlbumDetailsView {

    @InjectPresenter
    lateinit var albumDetailsPresenter: AlbumDetailsPresenter

    val albumDetailsAdapter: AlbumDetailsAdapter = AlbumDetailsAdapter()

    @Inject
    lateinit var artistInfoInteractor: ArtistInfoInteractor
    @Inject
    lateinit var schedulersProvider: SchedulersProvider
    @Inject
    lateinit var applicationProvider: ApplicationProvider

    init {
        TheApplication.getAppComponent().inject(this)
    }

    @ProvidePresenter
    internal fun providePresenter(): AlbumDetailsPresenter {
        return AlbumDetailsPresenter(artistInfoInteractor, schedulersProvider)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_album_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rv_album_details_tracks.setHasFixedSize(true)
        rv_album_details_tracks.layoutManager = LinearLayoutManager(context)
        rv_album_details_tracks.adapter = albumDetailsAdapter
        rv_album_details_tracks.isNestedScrollingEnabled = true
        rv_album_details_tracks.isFocusable = false

        val appCompatActivity = activity as AppCompatActivity
        appCompatActivity.setSupportActionBar(toolbar_album_details)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appCompatActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
        appCompatActivity.supportActionBar?.title = ""

        tv_album_details_error.setOnClickListener { albumDetailsPresenter.onErrorClicked()}


        arguments?.let { albumDetailsPresenter.setAlbumId(it.getInt(ALBUM_ID_ARGUMENT_KEY)) }
    }

    override fun setAlbumContributors(contributors: List<String>) {
        context?.let { context ->
            tv_album_details_artist.text = contributors.joinToString(context.getString(R.string.list_delimiter))
        }
    }

    override fun setAlbumPicture(pictureUrl: String) {

        Glide.with(iv_album_details_cover.context)
            .load(Uri.parse(pictureUrl))
            .placeholder(ColorDrawable(Color.WHITE)) // TODO: Use pictures
            .error(ColorDrawable(Color.RED)) // TODO: Use pictures
            .into(iv_album_details_cover)
    }

    override fun showGeneralError(show: Boolean) {
        tv_album_details_error.setText(R.string.error_general)
        tv_album_details_error.visibility = if (show) View.VISIBLE else View.GONE

    }

    override fun showNoNetworkError(show: Boolean) {
        tv_album_details_error.setText(R.string.error_no_network)
        tv_album_details_error.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showProgress(show: Boolean) {
        pb_album_details.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun setAlbumTitle(albumName: String) {
        tv_album_details_title.text = albumName
    }

    private fun convertTrackVolumeMapToAlbumItems(map:  Map<Int, TrackList>): List<AlbumItem> {
        val items: MutableList<AlbumItem> = ArrayList()

        map.keys.toSortedSet().forEach { volumeNumber ->
            if (map[volumeNumber]?.items?.isEmpty() == false) {
                items.add(AlbumItem(ItemType.VOLUME, getString(R.string.volume) + volumeNumber.toString(), null, null, null))
                var trackIndex = 1
                map[volumeNumber]?.items?.forEach { trackListItem ->
                    items.add(AlbumItem(ItemType.TRACK, null, trackListItem.trackTitle, trackListItem.trackContributors?.joinToString(getString(R.string.list_delimiter)), trackIndex++))
                }
            }
        }

        return items
    }

    override fun setTracksData(items: Map<Int, TrackList>) {
        setTracksData(convertTrackVolumeMapToAlbumItems(items))
    }

    private fun setTracksData(items: List<AlbumItem>) {
        albumDetailsAdapter.data = items
        albumDetailsAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "AlbumDetailsFragment"
        const val ALBUM_ID_ARGUMENT_KEY = "AlbumDetailsFragment.AlbumId"
    }

}