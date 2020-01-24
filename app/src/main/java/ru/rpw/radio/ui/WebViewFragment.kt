package ru.rpw.radio.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_web_view.*
import ru.rpw.radio.R


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
            val arg = it.get("urlWebView") as Int
            mainWebView.loadUrl(getString(arg))

            if (it.getBoolean("JavaScriptEnable")) {
                mainWebView.settings.javaScriptEnabled = true
                mainWebView.settings.allowFileAccess = true
            }
            when (getSharedPreference(mainWebView.context, "AppForOpenURL")) {
                "APP" -> {
                    mainWebView.webViewClient = WebViewClient()
                    mainWebView.settings.javaScriptEnabled = true
                    mainWebView.settings.allowFileAccess = true
                }
                "CHROME" -> {
                    mainWebView.webChromeClient = WebChromeClient()
                }
            }
        }
    }

    private fun getSharedPreference(context: Context, preferenceName: String): String? {
        val prefs = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        return prefs.getString(preferenceName, "NULL")
    }
}