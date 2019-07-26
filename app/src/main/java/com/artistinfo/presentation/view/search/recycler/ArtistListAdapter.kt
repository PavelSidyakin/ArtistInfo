package com.artistinfo.presentation.view.search.recycler

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.artistinfo.model.ArtistListItem

class ArtistListAdapter : PagedListAdapter<ArtistListItem, RecyclerView.ViewHolder>(artistListDiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ArtistListViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ArtistListViewHolder).bind(getItem(position))
    }

    companion object {
        val artistListDiffCallback = object : DiffUtil.ItemCallback<ArtistListItem>() {
            override fun areItemsTheSame(oldItem: ArtistListItem, newItem: ArtistListItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ArtistListItem, newItem: ArtistListItem): Boolean {
                return oldItem == newItem
            }
        }
    }



}