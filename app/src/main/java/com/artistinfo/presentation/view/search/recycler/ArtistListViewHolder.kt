package com.artistinfo.presentation.view.search.recycler

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.artistinfo.R
import com.artistinfo.model.ArtistListItem
import com.bumptech.glide.Glide
import android.net.Uri
import kotlinx.android.synthetic.main.recycler_item_artist_search.view.ci_artist_search_list_item_picture
import kotlinx.android.synthetic.main.recycler_item_artist_search.view.tv_artist_search_list_item_name

class ArtistListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(artistListItem: ArtistListItem?) {
        if (artistListItem != null) {
            itemView.tv_artist_search_list_item_name.text = artistListItem.name

            Glide.with(itemView.ci_artist_search_list_item_picture.context)
                .load(Uri.parse(artistListItem.pictureUrl))
                .placeholder(ColorDrawable(Color.WHITE)) // TODO: Use pictures
                .error(ColorDrawable(Color.RED)) // TODO: Use pictures
                .into(itemView.ci_artist_search_list_item_picture)
        }
    }

    companion object {
        fun create(parent: ViewGroup): ArtistListViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_artist_search, parent, false)
            return ArtistListViewHolder(view)
        }
    }
}