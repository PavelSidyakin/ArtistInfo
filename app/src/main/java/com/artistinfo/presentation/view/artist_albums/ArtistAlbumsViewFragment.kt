package com.artistinfo.presentation.view.artist_albums

import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
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
import com.artistinfo.model.AlbumListItem
import com.artistinfo.presentation.presenter.artist_albums.ArtistAlbumsPresenter
import com.artistinfo.presentation.view.MainActivity
import com.artistinfo.presentation.view.RecyclerViewOnItemClickListener
import com.artistinfo.presentation.view.artist_albums.recycler.ArtistAlbumsAdapter
import com.artistinfo.utils.rx.SchedulersProvider
import kotlinx.android.synthetic.main.layout_artist_albums_view.pb_artist_albums
import kotlinx.android.synthetic.main.layout_artist_albums_view.rv_artist_albums_list
import kotlinx.android.synthetic.main.layout_artist_albums_view.toolbar_artist_albums
import kotlinx.android.synthetic.main.layout_artist_albums_view.tv_artist_albums_error
import javax.inject.Inject

class ArtistAlbumsViewFragment : MvpAppCompatFragment(), ArtistAlbumsView {

    @InjectPresenter
    lateinit var artistAlbumsPresenter: ArtistAlbumsPresenter

    @Inject
    lateinit var artistInfoInteractor: ArtistInfoInteractor
    @Inject
    lateinit var schedulersProvider: SchedulersProvider
    @Inject
    lateinit var applicationProvider: ApplicationProvider

    private var artistAlbumsAdapter = ArtistAlbumsAdapter()

    private var currentArtistId: Int = 0

    init {
        TheApplication.getAppComponent().inject(this)
    }

    @ProvidePresenter
    internal fun providePresenter(): ArtistAlbumsPresenter {
        return ArtistAlbumsPresenter(artistInfoInteractor, schedulersProvider)
    }

    override fun updateDisplayData(data: PagedList<AlbumListItem>) {
        artistAlbumsAdapter.submitList(data)
    }

    override fun clearList() {
        artistAlbumsAdapter = ArtistAlbumsAdapter()
        rv_artist_albums_list.adapter = artistAlbumsAdapter
        artistAlbumsAdapter.notifyDataSetChanged()
    }

    override fun showGeneralError(show: Boolean) {
        tv_artist_albums_error.setText(R.string.error_general)
        tv_artist_albums_error.visibility = if (show) View.VISIBLE else View.GONE

    }

    override fun showNoNetworkError(show: Boolean) {
        tv_artist_albums_error.setText(R.string.error_no_network)
        tv_artist_albums_error.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showProgress(show: Boolean) {
        pb_artist_albums.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_artist_albums_view, container, false)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ARTIST_ID_STATE_KEY, currentArtistId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        savedInstanceState?.getInt(ARTIST_ID_STATE_KEY)?.let { currentArtistId = it }

        rv_artist_albums_list.adapter = artistAlbumsAdapter
        rv_artist_albums_list.layoutManager = GridLayoutManager(context, 2)
        rv_artist_albums_list.addOnItemTouchListener(RecyclerViewOnItemClickListener(applicationProvider.applicationContext, rv_artist_albums_list, object: RecyclerViewOnItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val item: AlbumListItem? = artistAlbumsAdapter.currentList?.get(position)
                if (item != null) {
                    artistAlbumsPresenter.onAlbumClicked(item)
                }
            }

            override fun onLongItemClick(view: View?, position: Int) {
            }
        }))

        val appCompatActivity = activity as AppCompatActivity
        appCompatActivity.setSupportActionBar(toolbar_artist_albums)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appCompatActivity.supportActionBar?.setDisplayShowHomeEnabled(true)

        arguments?.let {
            val argumentArtistId = it.getInt(ARTIST_ID_ARGUMENT_KEY)
            if (currentArtistId != argumentArtistId) {
                artistAlbumsPresenter.setArtistId(it.getInt(ARTIST_ID_ARGUMENT_KEY))
                currentArtistId = argumentArtistId
            }
        }

        tv_artist_albums_error.setOnClickListener { artistAlbumsPresenter.onErrorClicked()}
    }

    override fun openAlbumDetails(albumId: Int) {
        (activity as MainActivity).openAlbumDetails(albumId)
    }

    companion object {
        private const val TAG = "ArtistAlbumsView"
        const val FRAGMENT_TAG = "ArtistAlbumsViewFragment"
        const val ARTIST_ID_ARGUMENT_KEY = "ArtistAlbumsViewFragment.ArtistId"
        const val ARTIST_ID_STATE_KEY = "ArtistAlbumsViewFragment.ArtistId.State"

    }

}