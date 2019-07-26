package com.artistinfo.presentation.view.album.recycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.artistinfo.R

class AlbumDetailsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: List<AlbumItem>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return when(viewType){
            0 -> VolumeViewHolder(inflater.inflate(R.layout.item_album_details_volume, viewGroup, false))
            1 -> TrackViewHolder(inflater.inflate(R.layout.item_album_details_track, viewGroup, false))
            else -> throw IllegalStateException("Unknown item type")
        }
    }

    override fun getItemCount(): Int {
        return data?.size?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val albumItem = data?.get(position)
        when(albumItem?.itemType) {
            ItemType.VOLUME -> (holder as VolumeViewHolder).bind(albumItem.volumeTitle)
            ItemType.TRACK -> (holder as TrackViewHolder).bind(albumItem.trackTitle, albumItem.artistTitle, albumItem.number)
            null -> throw IllegalStateException("Unknown item type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        val albumItem = data?.get(position)
        return when(albumItem?.itemType) {
            ItemType.VOLUME -> 0
            ItemType.TRACK -> 1
            null -> throw IllegalStateException("Unknown item type")
        }
    }

}