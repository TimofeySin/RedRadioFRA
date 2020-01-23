package ru.rpw.radio.ui

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.rpw.radio.DataModelStatus
import ru.rpw.radio.R
import ru.rpw.radio.RetrofitServer
import ru.rpw.radio.SingletonMediaPlayer

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


        changeOrientation(root, mModelMedia.state)

        initControlMediaPlayer(root, mModelMedia)
        initUpdateNameOfTrack(1000)

        return root
    }

    private fun getRandomBackground(root: View): Drawable? {
        val backgroundList: Array<Int> =
            arrayOf(R.drawable.gegel, R.drawable.marx, R.drawable.engels, R.drawable.gegel2,R.drawable.lenin)
        val rand = Random()
        val back = backgroundList[rand.nextInt(backgroundList.size)]

        return getDrawable(root.context, back)
    }

    private fun changeOrientation(root: View, mediaState: SingletonMediaPlayer.StatePlayer) {
        val params = root.viewBorder.layoutParams
        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            root.main_layout.orientation = LinearLayout.HORIZONTAL
            root.buttonLinearLayout.orientation = LinearLayout.VERTICAL
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            params.width = 10
            initChangeLogo(root, mediaState)
        } else {
            root.main_layout.orientation = LinearLayout.VERTICAL
            root.buttonLinearLayout.orientation = LinearLayout.HORIZONTAL
            params.height = 10
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            initChangeLogo(root, mediaState)
        }
    }

    private fun initChangeLogo(root: View, mediaState: SingletonMediaPlayer.StatePlayer) {
        if (mediaState != SingletonMediaPlayer.StatePlayer.NOTREADY) {
            val animationUP = AnimationUtils.loadAnimation(root.context, R.anim.logo_transp_up)
            val animationDown = AnimationUtils.loadAnimation(root.context, R.anim.logo_transp_down)
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
            root.imageLogoFonHome.startAnimation(animationDown)
        }
    }

    //region Init MediaPlayer
    private fun setListenerOnMediaPlayer(
        root: View,
        mModelMedia: SingletonMediaPlayer,
        nextState: SingletonMediaPlayer.StatePlayer
    ) {
        mModelMedia.getMediaPlayer().setOnPreparedListener {
            mModelMedia.state = nextState
            initChangeLogo(root, mModelMedia.state)
            viewButtonMediaPlayer(root, mModelMedia)
            if (nextState == SingletonMediaPlayer.StatePlayer.PLAY) {
                mModelMedia.getMediaPlayer().start()
            }
        }
    }

    private fun initControlMediaPlayer(root: View, mModelMedia: SingletonMediaPlayer) {
        setListenerOnMediaPlayer(root, mModelMedia, SingletonMediaPlayer.StatePlayer.READY)
        viewButtonMediaPlayer(root, mModelMedia)
        root.imagePlayButton.setOnClickListener {
            mModelMedia.setWakeMode(root.context, true)
            if (mModelMedia.state == SingletonMediaPlayer.StatePlayer.PAUSE || mModelMedia.state == SingletonMediaPlayer.StatePlayer.READY) {
                mModelMedia.getMediaPlayer().start()
                setAirRecText("")
                mModelMedia.state = SingletonMediaPlayer.StatePlayer.PLAY
                viewButtonMediaPlayer(root, mModelMedia)
            } else if (mModelMedia.state == SingletonMediaPlayer.StatePlayer.PLAY) {
                mModelMedia.getMediaPlayer().pause()
                setAirRecText(getString(R.string.air_rec))
                mModelMedia.state = SingletonMediaPlayer.StatePlayer.PAUSE
                viewButtonMediaPlayer(root, mModelMedia)
            } else if (mModelMedia.state == SingletonMediaPlayer.StatePlayer.RESET) {
                mModelMedia.state = SingletonMediaPlayer.StatePlayer.NOTREADY
                setAirRecText("")
                mModelMedia.prepareAsync()
                setListenerOnMediaPlayer(root, mModelMedia, SingletonMediaPlayer.StatePlayer.PLAY)
                viewButtonMediaPlayer(root, mModelMedia)
            }
        }
        root.imageStopButton.setOnClickListener {
            if (mModelMedia.state == SingletonMediaPlayer.StatePlayer.PLAY || mModelMedia.state == SingletonMediaPlayer.StatePlayer.PAUSE) {
                mModelMedia.getMediaPlayer().reset()
                setAirRecText("")
                mModelMedia.getMediaPlayer().release()
                mModelMedia.state = SingletonMediaPlayer.StatePlayer.RESET
                viewButtonMediaPlayer(root, mModelMedia)
                mModelMedia.setWakeMode(root.context, false)
            }
        }
    }

    private fun viewButtonMediaPlayer(root: View, mModelMedia: SingletonMediaPlayer) {
        val transpAlfa = 0.5f
        when (mModelMedia.state) {
            SingletonMediaPlayer.StatePlayer.PLAY -> {
                root.imageStopButton.alpha = 1f
                root.imagePlayButton.alpha = 1f
                root.imagePlayButton.setImageResource(R.drawable.ic_pause_button)
                root.progressBar.visibility = ProgressBar.INVISIBLE
            }
            SingletonMediaPlayer.StatePlayer.PAUSE -> {
                root.imageStopButton.alpha = 1f
                root.imagePlayButton.alpha = 1f
                root.imagePlayButton.setImageResource(R.drawable.ic_play_button)
                root.progressBar.visibility = ProgressBar.INVISIBLE
            }
            SingletonMediaPlayer.StatePlayer.RESET -> {
                root.imageStopButton.alpha = transpAlfa
                root.imagePlayButton.setImageResource(R.drawable.ic_play_button)
                root.progressBar.visibility = ProgressBar.INVISIBLE
            }
            SingletonMediaPlayer.StatePlayer.NOTREADY -> {
                root.imageStopButton.alpha = transpAlfa
                root.imagePlayButton.alpha = transpAlfa
                root.imagePlayButton.setImageResource(R.drawable.ic_play_button)
                root.progressBar.visibility = ProgressBar.VISIBLE
            }
            SingletonMediaPlayer.StatePlayer.READY -> {
                root.imageStopButton.alpha = transpAlfa
                root.imagePlayButton.alpha = 1f
                root.imagePlayButton.setImageResource(R.drawable.ic_play_button)
                root.progressBar.visibility = ProgressBar.INVISIBLE
            }
        }
    }
    //endregion

    //region Name play track
    private fun setNameOfTrack(text: String) {
        this.view?.trackInfo?.let {
            if (text != it.text) {
                it.text = text
                it.isSelected = true
            }
        }
    }

    private fun setAirRecText(text: String){
        this.view?.airRec?.let {
            if (text != it.text) {
                it.text = text
            }
        }
    }


    private fun initUpdateNameOfTrack(period: Long) {
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
                    if (wResponse == null) {
                        setNameOfTrack(getString(R.string.technical_work))
                    } else {
                        setNameOfTrack("     " + wResponse.getSong())
                    }
                }
            }

            override fun onFailure(call: Call<DataModelStatus>, t: Throwable) {
                setNameOfTrack(getString(R.string.error_retrofit))
            }
        })

    }
    //endregion
}