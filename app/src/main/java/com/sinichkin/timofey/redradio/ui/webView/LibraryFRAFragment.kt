package com.sinichkin.timofey.redradio.ui.webView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sinichkin.timofey.redradio.R
import kotlinx.android.synthetic.main.fragment_about_rkr.view.*

class LibraryFRAFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_about_rkr, container, false)
        root.webViewGallery.loadUrl("http://www.rpw.ru/rkr/aboutrkr.html")
        return root
    }
}