package com.sinichkin.timofey.redradio.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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

        changeOrientation(root)
        val mModelMedia = SingletonMediaPlayer
        initPlayButtonAnimation(root, mModelMedia)

        initControlMediaPlayer(root, mModelMedia)
        initUpdateNameOfTrack(1000)

        return root
    }

    private fun changeOrientation(root: View) {
        val params = root.viewBorder.layoutParams
        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            root.main_layout.orientation = LinearLayout.HORIZONTAL
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            params.width = 10
        } else {
            root.main_layout.orientation = LinearLayout.VERTICAL
            params.height = 10
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }



    private fun initChangeLogo(root: View){

        val animUP = R.anim.play_button_transp_up
        val animDown = R.anim.play_button_transp_down
        val animationUP: Animation = AnimationUtils.loadAnimation(root.context, animUP)
        val animationDown: Animation = AnimationUtils.loadAnimation(root.context, animDown)
        root.imageLogoFonHome.startAnimation(animationDown)


        animationDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                root.imageLogoFonHome.setImageDrawable(getDrawable(root.context,R.drawable.rpw_logo))
                root.imageLogoFonHome.startAnimation(animationUP)
            }
            override fun onAnimationStart(animation: Animation?) {}
        })

        root.controlPlayerButton.startAnimation(animationDown)
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

    private fun initControlMediaPlayer(root: View, mModelMedia: SingletonMediaPlayer) {
        controlMediaPlayer(root, mModelMedia)
        mModelMedia.getMediaPlayer().setOnPreparedListener {
            mModelMedia.setMediaDone(true)
            initChangeLogo(root)
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
                root.controlPlayerButton.setPadding(resources.getDimension(R.dimen.activity_horizontal_margin).toInt()+15)
                root.controlPlayerButtonShadow.setPadding(resources.getDimension(R.dimen.activity_horizontal_margin).toInt()-1)
            } else {
                root.controlPlayerButton.setImageResource(R.drawable.ic_play_button)
                root.controlPlayerButton.setPadding(resources.getDimension(R.dimen.activity_horizontal_margin).toInt())
                root.controlPlayerButtonShadow.setPadding(resources.getDimension(R.dimen.activity_horizontal_margin).toInt()-16)
            }
        }
    }

//////////////////////////////////////////////
////нижняя строка название трека
private fun setNameOfTrack(text: String) {
    this.view?.let {
        if (text != it.trackInfo.text)      {
            it.trackInfo.text = text
            it.trackInfo.isSelected = true
        }
    }
}


    private fun initUpdateNameOfTrack(period:Long){
        val retrofit = initRetrofit()
        val timer = Timer()
        val monitor = object : TimerTask() {
            override fun run() {
                getNameOfTrack(retrofit)
            }
        }
        timer.schedule(monitor, 0, period)
    }

    private fun initRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://myradio24.com/")
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

}