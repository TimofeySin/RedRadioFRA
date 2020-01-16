package com.sinichkin.timofey.redradio.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sinichkin.timofey.redradio.R
import kotlinx.android.synthetic.main.fragment_web_view.*

class WebViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_web_view, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onResume() {
        super.onResume()

        arguments?.let {
            val arg = it.get("urlWebView")as Int
            mainWebView.loadUrl(getString(arg))

            if (it.getBoolean("JavaScriptEnable")){
                mainWebView.settings.javaScriptEnabled = true
                mainWebView.settings.allowFileAccess = true
            }
        }
    }
}