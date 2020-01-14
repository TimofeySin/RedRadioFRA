package com.sinichkin.timofey.redradio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sinichkin.timofey.redradio.R
import kotlinx.android.synthetic.main.fragment_about.view.*

class AboutFRAFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(R.layout.fragment_about, container, false)
        root.titleTextAbout.text = getString(R.string.menu_nav_fra)
        root.bigTextAbout.text = getString(R.string.about_fra)
        return  root
    }

}