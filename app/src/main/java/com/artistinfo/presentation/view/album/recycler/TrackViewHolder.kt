package com.artistinfo.presentation.view.album.recycler

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.artistinfo.R

class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private var title: TextView? = null
    private var artist: TextView? = null
    private var trackNumber: TextView? = null

    init {
        title = view.findViewById(R.id.tv_item_album_details_title)
        artist = view.findViewById(R.id.tv_item_album_details_artist)
        trackNumber = view.findViewById(R.id.tv_item_album_details_number)
    }

    fun bind(titleValue: String?, artistValue: String?, number: Int?) {
        title?.text = titleValue
        artist?.text = artistValue
        trackNumber?.text = number.toString()
    }
}