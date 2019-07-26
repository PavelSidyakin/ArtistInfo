package com.artistinfo.presentation.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.artistinfo.R
import com.artistinfo.TheApplication
import com.artistinfo.domain.ArtistInfoInteractor
import com.artistinfo.presentation.view.artist_albums.ArtistAlbumsViewFragment
import com.artistinfo.presentation.view.album.AlbumDetailsFragment
import com.artistinfo.presentation.view.search.ArtistSearchViewFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var artistInfoInteractor: ArtistInfoInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        TheApplication.getAppComponent().inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.main_activity_container, ArtistSearchViewFragment())

        fragmentTransaction.commit()
        fragmentManager.executePendingTransactions()
    }

    fun openArtistAlbumsView(artistId: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.addToBackStack(ArtistSearchViewFragment.FRAGMENT_TAG)
        val artistAlbumsViewFragment = ArtistAlbumsViewFragment()
        if (artistAlbumsViewFragment.arguments == null) {
            artistAlbumsViewFragment.arguments = Bundle()
        }
        artistAlbumsViewFragment.arguments?.putInt(ArtistAlbumsViewFragment.ARTIST_ID_ARGUMENT_KEY, artistId)
        fragmentTransaction.replace(R.id.main_activity_container, artistAlbumsViewFragment)

        fragmentTransaction.commit()
        fragmentManager.executePendingTransactions()
    }

    fun openAlbumDetails(albumId: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.addToBackStack(ArtistAlbumsViewFragment.FRAGMENT_TAG)
        val albumDetailsFragment = AlbumDetailsFragment()
        if (albumDetailsFragment.arguments == null) {
            albumDetailsFragment.arguments = Bundle()
        }
        albumDetailsFragment.arguments?.putInt(AlbumDetailsFragment.ALBUM_ID_ARGUMENT_KEY, albumId)
        fragmentTransaction.replace(R.id.main_activity_container, albumDetailsFragment)

        fragmentTransaction.commit()
        fragmentManager.executePendingTransactions()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount <= 0) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
