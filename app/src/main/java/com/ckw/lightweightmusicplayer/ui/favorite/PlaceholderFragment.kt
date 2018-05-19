package com.ckw.lightweightmusicplayer.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.ckw.lightweightmusicplayer.R

class PlaceholderFragment : android.support.v4.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val position = arguments!!.getInt(EXTRA_POSITION)
        val textViewPosition = inflater.inflate(R.layout.fragment_view_pager, container, false) as TextView
        textViewPosition.text = Integer.toString(position)
        textViewPosition.setBackgroundColor(COLORS[position - 1])

        return textViewPosition
    }

    companion object {
        val EXTRA_POSITION = "EXTRA_POSITION"
        private val COLORS = intArrayOf(-0xcc4a1b, -0x559934, -0x663400, -0x44cd, -0xbbbc)
    }
}
