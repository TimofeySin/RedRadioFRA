package com.sinichkin.timofey.redradio.ui

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.sinichkin.timofey.redradio.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class HomeFragment : Fragment() {
private val periodUpdateRunText :Long = 1000
private var errorCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        changeOrientation(root)
        val mModelMedia = SingltonMediaPlayer
        initVolumeButton(root, mModelMedia)
        initPlayButtonAnimation(root, mModelMedia)

        initControlMediaPlayer(root, mModelMedia)
        initUpdateNameOfTrack(periodUpdateRunText)
        //initUpdateNameOfTrack(periodUpdateRunText)

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

///////// Все связанное со звуком
//////////////////////////////////////////////

    private fun initVolumeButton(root: View, mModelMedia: SingltonMediaPlayer) {
        root.controlVolumeButton.setSecondView(root.imageVolumeButton)
        root.controlVolumeButton.setOnSliderMovedListener(object :
            VolumeButtonView.OnSliderMovedListener {

            override fun onSliderMoved(pos: Double, firstPos: Double, startRotation: Float, clock:Int,quarter: Int) {
                val posAngle = root.controlVolumeButton.convertClockToPosition(pos.toFloat())
                val firstPosAngle =
                    root.controlVolumeButton.convertClockToPosition(firstPos.toFloat())
                val percent = posAngle * 100f / 360f
                val endPercent = root.controlVolumeButton.roundAngle(
                    (startRotation - (firstPosAngle - posAngle)),
                    root.imageVolumeButton.rotation,clock,quarter
                )//startRotation:$startRotation|firstPosAngle:$firstPosAngle|
                Log.d("INB","HOME:   pos  :$pos|posAngle:$posAngle|endPercent:$endPercent|rotation:"+root.imageVolumeButton.rotation.toString()+"|sum:"+(startRotation - (firstPosAngle - posAngle)).toString())
               if(clock==2) {
                   root.imageVolumeButton.rotation += pos.toFloat()//endPercent
               } else {root.imageVolumeButton.rotation -= pos.toFloat()}
                mModelMedia.getMediaPlayer().setVolume(percent / 100 * 2f, percent / 100 * 2f)
            }
        })
    }

    private fun initPlayButtonAnimation(root: View, mModelMedia: SingltonMediaPlayer) {
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

    private fun initControlMediaPlayer(root: View, mModelMedia: SingltonMediaPlayer) {
        controlMediaPlayer(root, mModelMedia)
        mModelMedia.getMediaPlayer().setOnPreparedListener {
            mModelMedia.setMediaDone(true)
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

    private fun controlMediaPlayer(root: View, mModelMedia: SingltonMediaPlayer) {
        if (mModelMedia.getMediaDone()) {
            if (mModelMedia.getMediaPlayer().isPlaying) {
                root.controlPlayerButton.setImageResource(R.drawable.ic_pause_button)
                root.controlPlayerButton.setPadding(40)
            } else {
                root.controlPlayerButton.setImageResource(R.drawable.ic_play_button)
                root.controlPlayerButton.setPadding(0)
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

//    private fun initUpdateNameOfTrack(period:Long){
//        val retrofit = initRetrofit()
//        val timer = Timer()
//        val monitor = object : TimerTask() {
//            override fun run() {
//                getNameOfTrack(retrofit)
//                if (errorCount>2)
//                {    timer.cancel()}
//            }
//        }
//        timer.schedule(monitor, 0, period)
//    }
//
    private fun initUpdateNameOfTrack(period:Long) {
        val retrofit = initRetrofit()
        val myThread = Thread(Runnable {
            while (true) {
                getNameOfTrack(retrofit)
                try {
                    Thread.sleep(period)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        })
        myThread.start()
    }

//    private fun getStatusRunnable(period: Long){
//        val retrofit = initRetrofit()
//        val handler =  Handler()
//        object : Runnable {
//            override fun run() {
//                handler.postDelayed(this, period*10) // here is self calling            }
//                getNameOfTrack(retrofit)
//        }
//
//    }
//    }

    private fun initRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://myradio24.org/")
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
                errorCount++
                setNameOfTrack(t.message.toString())   ///+
            }
        })
    }

//////////////////////////////////////////////

}