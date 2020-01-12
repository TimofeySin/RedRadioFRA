package com.sinichkin.timofey.redradio.ui.webView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sinichkin.timofey.redradio.R
import kotlinx.android.synthetic.main.fragment_articles_rpr.view.*

class ArticlesRPRFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_articles_rpr, container, false)
        root.articlesRPR_xml.loadUrl("https://www.r-p-w.ru/ustav.html")
        return root
    }
}