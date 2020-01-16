package com.sinichkin.timofey.redradio.ui

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.sinichkin.timofey.redradio.DataModelStatus
import com.sinichkin.timofey.redradio.R
import com.sinichkin.timofey.redradio.RetrofitServer
import com.sinichkin.timofey.redradio.SingletonMediaPlayer
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        getRandomBackground(root)?.let { root.imageLogoFonHome.setImageDrawable(it) }
        
        val mModelMedia = SingletonMediaPlayer
        changeOrientation(root,mModelMedia.getMediaDone())
        initPlayButtonAnimation(root, mModelMedia)
        initControlMediaPlayer(root, mModelMedia)
        initUpdateNameOfTrackRun(1000)

        return root
    }

    private fun getRandomBackground(root: View): Drawable? {
        val backgroundList : Array<Int> = arrayOf(R.drawable.gegel, R.drawable.marx)
        val rand = Random()
        val back =  backgroundList[rand.nextInt(backgroundList.size)]

        return getDrawable(root.context,back)
    }

    private fun changeOrientation(root: View,mediaDone:Boolean) {
        val params = root.viewBorder.layoutParams
        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            root.main_layout.orientation = LinearLayout.HORIZONTAL
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            params.width = 10
            initChangeLogo(root,mediaDone)
        } else {
            root.main_layout.orientation = LinearLayout.VERTICAL
            params.height = 10
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            initChangeLogo(root,mediaDone)
        }
    }

    private fun initChangeLogo(root: View,mediaDone:Boolean) {
        if (mediaDone) {
            val animUP = R.anim.play_button_transp_up
            val animDown = R.anim.play_button_transp_down
            val animationUP = AnimationUtils.loadAnimation(root.context, animUP)
            val animationDown = AnimationUtils.loadAnimation(root.context, animDown)
            root.imageLogoFonHome.startAnimation(animationDown)
            animationDown.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    root.imageLogoFonHome.setImageDrawable(
                        getDrawable(
                            root.context,
                            R.drawable.rpw_logo
                        )
                    )
                    root.imageLogoFonHome.startAnimation(animationUP)
                }
                override fun onAnimationStart(animation: Animation?) {}
            })
            root.controlPlayerButton.startAnimation(animationDown)
        }
    }

    private fun initPlayButtonAnimation(root: View, mModelMedia: SingletonMediaPlayer) {
        val anim = R.anim.play_button_transp
        val animation: Animation = AnimationUtils.loadAnimation(root.context, anim)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                if (mModelMedia.getMediaDone()) {
                    root.controlPlayerButton.clearAnimation()
                } else {
                    animation!!.setAnimationListener(this)
                    root.controlPlayerButton.startAnimation(animation)
                }
            }
            override fun onAnimationStart(animation: Animation?) {}
        })
        root.controlPlayerButton.startAnimation(animation)
    }

    //region Init MediaPlayer
    private fun initControlMediaPlayer(root: View, mModelMedia: SingletonMediaPlayer) {
        controlMediaPlayer(root, mModelMedia)
        mModelMedia.getMediaPlayer().setOnPreparedListener {
            mModelMedia.setMediaDone(true)
            initChangeLogo(root,mModelMedia.getMediaDone())
        }
        root.controlPlayerButton.setOnClickListener {
            if (mModelMedia.getMediaPlayer().isPlaying) {
                mModelMedia.getMediaPlayer().pause()
            } else {
                mModelMedia.getMediaPlayer().start()
            }
            controlMediaPlayer(root, mModelMedia)
        }
    }

    private fun controlMediaPlayer(root: View, mModelMedia: SingletonMediaPlayer) {
        if (mModelMedia.getMediaDone()) {
            if (mModelMedia.getMediaPlayer().isPlaying) {
                root.controlPlayerButton.setImageResource(R.drawable.ic_pause_button)
                root.controlPlayerButton.setPadding(resources.getDimension(R.dimen.activity_horizontal_margin).toInt() + 15)
                root.controlPlayerButtonShadow.setPadding(resources.getDimension(R.dimen.activity_horizontal_margin).toInt() - 1)
            } else {
                root.controlPlayerButton.setImageResource(R.drawable.ic_play_button)
                root.controlPlayerButton.setPadding(resources.getDimension(R.dimen.activity_horizontal_margin).toInt())
                root.controlPlayerButtonShadow.setPadding(resources.getDimension(R.dimen.activity_horizontal_margin).toInt() - 16)
            }
        }
    }
    //endregion

    //region Name play track
    private fun setNameOfTrack(text: String) {
        this.view?.let {
            if (text != it.trackInfo.text) {
                it.trackInfo.text = text
                it.trackInfo.isSelected = true
            }
        }
    }

    private fun initUpdateNameOfTrackRun(period: Long) {
        val retrofit = initRetrofit()
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                getNameOfTrack(retrofit)
                handler.postDelayed(this, 10 * period)
            }
        }
        runnable.run()
    }

    private fun initRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getString(R.string.stream_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getNameOfTrack(retrofit: Retrofit) {
        val service = retrofit.create(RetrofitServer::class.java)
        val call = service.getStatus()
        call.enqueue(object : Callback<DataModelStatus> {
            override fun onResponse(
                call: Call<DataModelStatus>,
                response: Response<DataModelStatus>
            ) {
                if (response.code() == 200) {
                    val wResponse = response.body()
                    setNameOfTrack("     " + wResponse!!.getSong())
                }
            }

            override fun onFailure(call: Call<DataModelStatus>, t: Throwable) {
                setNameOfTrack(getString(R.string.error_retrofit))
            }
        })

    }
    //endregion
}