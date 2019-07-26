package com.artistinfo.presentation.view.artist_albums.recycler

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.artistinfo.model.AlbumListItem

class ArtistAlbumsAdapter: PagedListAdapter<AlbumListItem, RecyclerView.ViewHolder>(artistListDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ArtistAlbumsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ArtistAlbumsViewHolder).bind(getItem(position))
    }

    companion object {
        val artistListDiffCallback = object : DiffUtil.ItemCallback<AlbumListItem>() {
            override fun areItemsTheSame(oldItem: AlbumListItem, newItem: AlbumListItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: AlbumListItem, newItem: AlbumListItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}