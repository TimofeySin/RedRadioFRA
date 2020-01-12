package com.sinichkin.timofey.redradio.ui.webView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sinichkin.timofey.redradio.R
import kotlinx.android.synthetic.main.fragment_web_view.view.*

class ProfDocFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_web_view, container, false)

        root.mainWebView.settings.javaScriptEnabled = true
        root.mainWebView.settings.allowFileAccess = true
     //   root.mainWebView.settings.allowContentAccess = true

        root.mainWebView.loadUrl(getString(R.string.menu_url_prof_doc))
        return root
    }
}