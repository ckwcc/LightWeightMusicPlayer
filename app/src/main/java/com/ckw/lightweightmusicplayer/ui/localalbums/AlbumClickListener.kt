package com.ckw.lightweightmusicplayer.ui.localalbums

/**
 * Created by ckw
 * on 2018/5/31.
 */
interface AlbumClickListener {
    fun setOnItemClick(position: Int, viewHolder: LocalAlbumsAdapter.ViewHolder)
}
