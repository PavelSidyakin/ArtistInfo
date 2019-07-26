package com.artistinfo.presentation.view.artist_albums.recycler

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.artistinfo.R
import com.artistinfo.model.AlbumListItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycler_item_artist_albums.view.iv_artist_album
import kotlinx.android.synthetic.main.recycler_item_artist_albums.view.tv_artist_album_contributors
import kotlinx.android.synthetic.main.recycler_item_artist_albums.view.tv_artist_album_title

class ArtistAlbumsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(albumListItem: AlbumListItem?) {
        if (albumListItem != null) {

            itemView.tv_artist_album_title.text = albumListItem.albumTitle

            //itemView.iv_artist_album.setImageBitmap(albumListItem.picture)

            Glide.with(itemView.iv_artist_album.context)
                .load(Uri.parse(albumListItem.pictureUrl))
                .placeholder(ColorDrawable(Color.WHITE)) // TODO: Use pictures
                .error(ColorDrawable(Color.RED)) // TODO: Use pictures
                .into(itemView.iv_artist_album)

            itemView.tv_artist_album_contributors.text = albumListItem.contributors?.joinToString(itemView.context.getString(R.string.list_delimiter))
        }
    }

    companion object {
        fun create(parent: ViewGroup): ArtistAlbumsViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_artist_albums, parent, false)
            return ArtistAlbumsViewHolder(view)
        }
    }
}