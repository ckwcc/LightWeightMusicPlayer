package com.ckw.lightweightmusicplayer.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide

import com.ckw.lightweightmusicplayer.R
import kotlinx.android.synthetic.main.fragment_music_play.view.*

class PlaceholderFragment : android.support.v4.app.Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val position = arguments!!.getInt("position")
        val title = arguments!!.getString("title")
        val artist = arguments!!.getString("artist")
        val album = arguments!!.getString("album")

        val relativeLayout = inflater.inflate(R.layout.fragment_view_pager, container, false) as RelativeLayout
        val tvTitle: TextView = relativeLayout.findViewById(R.id.tv_title)
        tvTitle.text = title
        val tvArtist: TextView = relativeLayout.findViewById(R.id.tv_artist)
        tvArtist.text = artist
        val ivAlbum: ImageView = relativeLayout.findViewById(R.id.iv_album)
        if(album != null && album != ""){
            Glide.with(context!!).load(album)
                    .into(ivAlbum)
        }

        val textContainer = relativeLayout.findViewById(R.id.rl_text_container) as RelativeLayout
        textContainer.setBackgroundColor(COLORS[position % 5])

        return relativeLayout
    }

    companion object {
        val EXTRA_POSITION = "EXTRA_POSITION"
        val COLORS = intArrayOf(-0xcc4a1b, -0x559934, -0x663400, -0x44cd, -0xbbbc)
    }
}
