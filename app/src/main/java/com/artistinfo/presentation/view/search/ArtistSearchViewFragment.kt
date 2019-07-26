package com.artistinfo.presentation.view.search

import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.artistinfo.R
import com.artistinfo.TheApplication
import com.artistinfo.domain.ArtistInfoInteractor
import com.artistinfo.domain.data.ApplicationProvider
import com.artistinfo.model.ArtistListItem
import com.artistinfo.presentation.presenter.search.SearchArtistPresenter
import com.artistinfo.presentation.view.MainActivity
import com.artistinfo.presentation.view.RecyclerViewOnItemClickListener
import com.artistinfo.presentation.view.search.recycler.ArtistListAdapter
import com.artistinfo.utils.rx.SchedulersProvider
import kotlinx.android.synthetic.main.layout_artist_search_view.pb_artist_search
import kotlinx.android.synthetic.main.layout_artist_search_view.rv_artist_search_list
import kotlinx.android.synthetic.main.layout_artist_search_view.sv_artist_search_view
import kotlinx.android.synthetic.main.layout_artist_search_view.tv_artist_search_error
import javax.inject.Inject

class ArtistSearchViewFragment : MvpAppCompatFragment(), ArtistSearchView {


    @InjectPresenter
    lateinit var searchArtistPresenter: SearchArtistPresenter

    @Inject
    lateinit var artistInfoInteractor: ArtistInfoInteractor
    @Inject
    lateinit var schedulersProvider: SchedulersProvider
    @Inject
    lateinit var applicationProvider: ApplicationProvider

    private var artistListAdapter = ArtistListAdapter()

    init {
        TheApplication.getAppComponent().inject(this)
    }

    @ProvidePresenter
    internal fun providePresenter(): SearchArtistPresenter {
        return SearchArtistPresenter(schedulersProvider, artistInfoInteractor)
    }

    override fun updateDisplayData(data: PagedList<ArtistListItem>) {
        artistListAdapter.submitList(data)
    }

    override fun clearList() {
        artistListAdapter = ArtistListAdapter()
        rv_artist_search_list.adapter = artistListAdapter
        artistListAdapter.notifyDataSetChanged()
    }

    override fun showGeneralError(show: Boolean) {
        tv_artist_search_error.setText(R.string.error_general)
        tv_artist_search_error.visibility = if (show) View.VISIBLE else View.GONE

    }

    override fun showNoNetworkError(show: Boolean) {
        tv_artist_search_error.setText(R.string.error_no_network)
        tv_artist_search_error.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showProgress(show: Boolean) {
        pb_artist_search.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun openArtistAlbums(artistId: Int) {
        (activity as MainActivity).openArtistAlbumsView(artistId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        val view = inflater.inflate(R.layout.layout_artist_search_view, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        rv_artist_search_list.adapter = artistListAdapter
        rv_artist_search_list.addOnItemTouchListener(RecyclerViewOnItemClickListener(applicationProvider.applicationContext, rv_artist_search_list, object: RecyclerViewOnItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val item: ArtistListItem?  = artistListAdapter.currentList?.get(position)
                if (item != null) {
                    searchArtistPresenter.onArtistClicked(item)
                }
            }

            override fun onLongItemClick(view: View?, position: Int) {
            }
        }))

        tv_artist_search_error.setOnClickListener { searchArtistPresenter.onErrorClicked()}
        sv_artist_search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                searchArtistPresenter.onSearchTextChanged(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                searchArtistPresenter.onSearchTextSubmitted(query)
                return true
            }

        })
    }

    companion object {
        private const val TAG = "ArtistSearchFragment"
        const val FRAGMENT_TAG = "ArtistSearchFragment"
    }
}
