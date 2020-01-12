package com.sinichkin.timofey.redradio.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.sinichkin.timofey.redradio.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        changeOrientation(root)
        val mModelMedia = SingltonMediaPlayer
        initVolumeButton(root, mModelMedia)
        if (isNetworkConnected(root.context)) {

            testAnimation(root, mModelMedia)
            initControlMediaPlayer(root, mModelMedia)

            val retrofit = initRetrofit()
            getStatus(root, retrofit)

        }
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

    private fun initVolumeButton(root: View, mModelMedia: SingltonMediaPlayer) {
        root.controlVolumeButton.setSecondView(root.imageVolumeButton)
        root.controlVolumeButton.setOnSliderMovedListener(object :
            VolumeButtonView.OnSliderMovedListener {
            override fun onSliderMoved(pos: Double, firstPos: Double, startRotation: Float) {

                val posAngle = root.controlVolumeButton.convertClockToPosition(pos.toFloat())
                val firstPosAngle =
                    root.controlVolumeButton.convertClockToPosition(firstPos.toFloat())
                val procent = posAngle * 100f / 360f


                var endProcent = roundAngle((startRotation - (firstPosAngle - posAngle)))
                Log.d("PROCENT","endProcent:$endProcent/Anglein:"+(startRotation - (firstPosAngle - posAngle)))
                if (endProcent > 180f && root.imageVolumeButton.rotation<180f) {
                    endProcent = 180f
                }
                Log.d("PROCENT","finich:$endProcent / rotation:"+root.imageVolumeButton.rotation)
                root.imageVolumeButton.rotation = endProcent
                mModelMedia.getMediaPlayer().setVolume(procent / 100*2f, procent / 100*2f)

            }
        })
    }

    private fun roundAngle(angle: Float): Float {
        if (angle < 0f || angle > 360f) {return 0f}
        return angle
    }

    private fun initControlMediaPlayer(root: View, mModelMedia: SingltonMediaPlayer) {
        controlMediaPlayer(root, mModelMedia)
        mModelMedia.getMediaPlayer().setOnPreparedListener {
            mModelMedia.setMediaDone(true)
            // controlMediaPlayer(root,mModelMedia)
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
            //  root.controlPlayerButton.alpha = 1f
            if (mModelMedia.getMediaPlayer().isPlaying) {
                root.controlPlayerButton.setImageResource(R.drawable.ic_pause_button)
//              //  root.controlPlayerButton.setPadding(80)
            } else {
                root.controlPlayerButton.setImageResource(R.drawable.ic_play_button)
//               // root.controlPlayerButton.setPadding(40)
            }
        } else {
            //root.controlPlayerButton.setImageResource(R.drawable.ic_play_button)
            //root.controlPlayerButton.setPadding(40)
            val animation: Animation =
                AnimationUtils.loadAnimation(root.context, R.anim.play_button_scale)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    if (mModelMedia.getMediaDone()) {
                        val animation: Animation =
                            AnimationUtils.loadAnimation(root.context, R.anim.play_button_scale)
                        animation.setAnimationListener(this)
                        root.controlPlayerButton.startAnimation(animation)
                    } else {
                        root.controlPlayerButton.clearAnimation()
                    }
                }

                override fun onAnimationStart(animation: Animation?) {}
            })

            root.controlPlayerButton.startAnimation(animation)

        }
    }

    private fun initRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://myradio24.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

    private fun retrofit(root: View, retrofit: Retrofit) {

        val service = retrofit.create(RetrofitServer::class.java)
        val call = service.getStatus()
        call.enqueue(object : Callback<DataModelStatus> {
            override fun onResponse(
                call: Call<DataModelStatus>,
                response: Response<DataModelStatus>
            ) {
                if (response.code() == 200) {
                    val wResponse = response.body()
                    val textString = "     " + wResponse!!.getSong()
                    if (textString != root.trackInfo.text) {
                        root.trackInfo.text = textString
                    }
                    root.trackInfo.isSelected = true

                }
            }

            override fun onFailure(call: Call<DataModelStatus>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })


    }

    private fun getStatus(root: View, retrofit: Retrofit) {
        val myThread = Thread(Runnable {
            while (true) {
                retrofit(root, retrofit)
                try {
                    Thread.sleep(10000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        })
        myThread.start()
    }

    private fun testAnimation(root: View, mModelMedia: SingltonMediaPlayer) {
        val anim = R.anim.play_button_transp
        val animation: Animation = AnimationUtils.loadAnimation(root.context, anim)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                if (mModelMedia.getMediaDone()) {
                    root.controlPlayerButton.clearAnimation()
                } else {
                    val animation: Animation = AnimationUtils.loadAnimation(root.context, anim)
                    animation.setAnimationListener(this)
                    root.controlPlayerButton.startAnimation(animation)

                }
            }

            override fun onAnimationStart(animation: Animation?) {}
        })

        root.controlPlayerButton.startAnimation(animation)
    }

    private fun isNetworkConnected(context: Context): Boolean {
//        val cm =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//            if (Build.VERSION.SDK_INT < 23) {
//                val ni = cm.activeNetworkInfo
//                if (ni != null) {
//                    return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
//                }
//            } else {
//                val n = cm.activeNetwork
//                if (n != null) {
//                    val nc = cm.getNetworkCapabilities(n)
//                    return nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
//                        NetworkCapabilities.TRANSPORT_WIFI
//                    )
//                }
//            }
//
//        return false
        return true
    }
}