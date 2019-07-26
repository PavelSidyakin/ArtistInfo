package com.artistinfo.presentation.view.album.recycler

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.artistinfo.R

class VolumeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var title: TextView? = null

    init {
        title = view.findViewById(R.id.tv_item_album_details_volume_title)
    }

    fun bind(volumeTitle: String?) {
        title?.text = volumeTitle
    }

}