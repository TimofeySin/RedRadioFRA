package ru.rpw.radio.ui

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.rpw.radio.DataModelStatus
import ru.rpw.radio.PlayerService
import ru.rpw.radio.R
import ru.rpw.radio.RetrofitServer
import ru.rpw.radio.collection.CollectionViewModel
import ru.rpw.radio.core.Station
import ru.rpw.radio.helpers.LogHelper
import ru.rpw.radio.helpers.StationListHelper
import ru.rpw.radio.helpers.StorageHelper
import ru.rpw.radio.helpers.TransistorKeys.*
import java.net.URL
import java.util.*

class HomeFragment : Fragment() {

    /* Define log tag */
    private val LOG_TAG: String = HomeFragment::class.java.getSimpleName()

    private lateinit var mActivity: Activity
    private var mCurrentStation: Station? = null
    private var mPlayerServiceStation: Station? = null
    private var mCollectionViewModel: CollectionViewModel? = null
    private var mPlaybackStateChangedReceiver: BroadcastReceiver? = null
    private var mMetadataChangedReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get activity and application contexts
        mActivity = activity!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        getRandomBackground(root)?.let { root.imageLogoFonHome.setImageDrawable(it) }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUiAccordingWithOrientation()
        initUpdateNameOfTrack(1000)

        val thread = Thread {
            val folder = StorageHelper.getCollectionDirectory(mActivity)
            val streamUrl = "https://myradio24.org/zuek1917"
            mCurrentStation = Station(folder, URL(Uri.parse(streamUrl).toString().trim()))
            mCurrentStation!!.stationName = getString(R.string.menu_nav_red_radio)
            mCurrentStation!!.resetState()
            Handler(Looper.getMainLooper()).post {
                setupStationPlaybackButtonState(mCurrentStation!!)
                setupPlayer(mCurrentStation!!)
                animatePlaybackButtonStateTransition(mCurrentStation!!)
                initPlayButtonListener()
                // observe changes in LiveData
                mCollectionViewModel = ViewModelProviders.of((mActivity as AppCompatActivity)).get(
                    CollectionViewModel::class.java
                )

                mCollectionViewModel!!.playerServiceStation
                    .observe(mActivity as LifecycleOwner, createStationObserver())
            }
        }
        thread.start()


    }

    override fun onResume() {
        super.onResume()

        // switch volume control from ringtone to music
        mActivity.volumeControlStream = AudioManager.STREAM_MUSIC

        // handles the activity's intent
        val intent = mActivity.intent
        if (ACTION_SHOW_PLAYER == intent.action) {
            handleShowPlayer(intent)
        }
        initializeBroadcastReceivers()
    }

    override fun onPause() {
        super.onPause()
        unregisterBroadcastReceivers(requireContext())
    }

    private fun getRandomBackground(root: View): Drawable? {
        val backgroundList: Array<Int> =
            arrayOf(R.drawable.gegel, R.drawable.marx, R.drawable.engels, R.drawable.gegel2,R.drawable.lenin)
        val rand = Random()
        val back = backgroundList[rand.nextInt(backgroundList.size)]

        return getDrawable(root.context, back)
    }

    private fun updateUiAccordingWithOrientation() {
        val params = viewBorder.layoutParams
        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            main_layout.orientation = LinearLayout.HORIZONTAL
            buttonLinearLayout.orientation = LinearLayout.VERTICAL
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            params.width = 10
        } else {
            main_layout.orientation = LinearLayout.VERTICAL
            buttonLinearLayout.orientation = LinearLayout.HORIZONTAL
            params.height = 10
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
        }

    }

    /* Initiates the rotation animation of the playback button  */
    private fun animatePlaybackButtonStateTransition(station: Station) {
        if (isAdded) { // toggle views needed for active playback
            when (station.playbackState) {
                PLAYBACK_STATE_STOPPED -> {
                    val rotateCounterClockwise =
                        AnimationUtils.loadAnimation(
                            mActivity,
                            R.anim.rotate_counterclockwise_fast
                        )
                    rotateCounterClockwise.setAnimationListener(createAnimationListener(station))
                    imagePlayButton.startAnimation(rotateCounterClockwise)
                }
                PLAYBACK_STATE_LOADING_STATION -> {
                    val rotateClockwise =
                        AnimationUtils.loadAnimation(
                            mActivity,
                            R.anim.rotate_clockwise_slow
                        )
                    rotateClockwise.setAnimationListener(createAnimationListener(station))
                    imagePlayButton.startAnimation(rotateClockwise)
                }
                PLAYBACK_STATE_STARTED -> {
                    setupStationPlaybackButtonState(station)
                }
            }
        }
    }

    /* Creates AnimationListener for playback button */
    private fun createAnimationListener(station: Station): AnimationListener? {
        return object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) { // set up button symbol and playback indicator afterwards
                setupStationPlaybackButtonState(station)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        }
    }

    /* Setup playback button state */
    private fun setupStationPlaybackButtonState(station: Station) {
        if (isAdded) { // toggle views needed for active playback
            when (station.playbackState) {
                PLAYBACK_STATE_STOPPED -> {
                    imagePlayButton.setImageResource(R.drawable.ic_play_button)
                    imagePlayButton.contentDescription = mActivity.getString(R.string.descr_playback_button_play)
                }
                PLAYBACK_STATE_LOADING_STATION -> {
                    imagePlayButton.setImageResource(R.drawable.ic_stop_button)
                    imagePlayButton.setContentDescription(mActivity.getString(R.string.descr_playback_button_stop))
                }
                PLAYBACK_STATE_STARTED -> {
                    imagePlayButton.setImageResource(R.drawable.ic_stop_button)
                    imagePlayButton.setContentDescription(mActivity.getString(R.string.descr_playback_button_stop))
                }
            }
        }
    }

    private fun initPlayButtonListener() {
        imagePlayButton.setOnClickListener {
            togglePlayback(mCurrentStation!!, false)
        }
    }

    //region Name play track
    private fun setNameOfTrack(text: String) {
        this.view?.trackInfo?.let {
            if (text != it.text) {
                it.text = text
                it.isSelected = true
            }
        }
    }

    private fun setNameOfTrack(id: Int) {
        this.view?.trackInfo?.let {
            val text = getString(id)
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
                        setNameOfTrack(R.string.technical_work)
                    } else {
                        setNameOfTrack("     " + wResponse.getSong())
                    }
                }
            }

            override fun onFailure(call: Call<DataModelStatus>, t: Throwable) {
                setNameOfTrack(R.string.error_retrofit)
            }
        })

    }
    //endregion

    private fun togglePlayback(station: Station, isLongPress: Boolean) {
        if (station.playbackState != PLAYBACK_STATE_STOPPED) { // stop player service using intent
            stopPlayback()
            // if long press -> inform user
            if (isLongPress) {
                longPressFeedback(R.string.toastmessage_long_press_playback_stopped)
            }
        } else { // start player service using intent
            startPlayback(station)
            // if long press -> inform user
            if (isLongPress) {
                longPressFeedback(R.string.toastmessage_long_press_playback_started)
            }
        }
    }

    /* start player service using intent */
    private fun startPlayback(station: Station) {
        val intent = Intent(requireActivity(), PlayerService::class.java)
        intent.action = ACTION_PLAY
        intent.putExtra(EXTRA_STATION, station)
        requireActivity().startService(intent)
        mPlayerServiceStation = station
        LogHelper.v(LOG_TAG, "Starting player service.")
    }


    /* Stop player service using intent */
    private fun stopPlayback() {
        val intent = Intent(requireActivity(), PlayerService::class.java)
        intent.action = ACTION_STOP
        requireActivity().startService(intent)
        LogHelper.v(LOG_TAG, "Stopping player service.")
    }

    /* Inform user and give haptic feedback (vibration) */
    private fun longPressFeedback(stringResource: Int) { // inform user
        if (stringResource != EMPTY_STRING_RESOURCE) {
            Toast.makeText(requireContext(), stringResource, Toast.LENGTH_LONG).show()
        }
        // vibrate 50 milliseconds
        val v =
            requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(50)
        //            v.vibrate(VibrationEffect.createOneShot(50, DEFAULT_AMPLITUDE)); // todo check if there is a support library vibrator
    }

    /* Handles intent to show player from notification or from shortcut */
    private fun handleShowPlayer(intent: Intent) {
        var station: Station? = null
        var startPlayback = false
        // CASE: user tapped on notification
        if (intent.hasExtra(EXTRA_STATION)) { // get station from notification
            station = intent.getParcelableExtra(EXTRA_STATION)
            mPlayerServiceStation = station
            startPlayback = false
        }

        // clear the intent, show player and start playback if requested
        if (station != null) {
            intent.action = ""
            setupPlayer(station)
            if (startPlayback) {
                startPlayback(station)
            }
        } else {
            Toast.makeText(
                mActivity,
                getString(R.string.toastalert_station_not_found),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /* Setup player visually */
    private fun setupPlayer(station: Station) {
        if (isAdded) { // update current station
            mCurrentStation = station
            setupStationPlaybackButtonState(station)
        }
    }

    /* Creates an observer for station from player service stored as LiveData */
    private fun createStationObserver(): Observer<Station?> {
        return Observer { newStation ->
            // check if the station currently selected has been changed
            if (mCurrentStation != null && newStation != null &&
                mCurrentStation!!.streamUri.equals(newStation.streamUri)
            ) {
                val newName = newStation.stationName
                val newMetaData = newStation.metadata
                val oldName = mCurrentStation!!.stationName
                val oldMetaData = mCurrentStation!!.metadata
                // CASE: PLAYBACK STATE
                if (mCurrentStation!!.playbackState != newStation.playbackState) {
                    animatePlaybackButtonStateTransition(newStation)
                }
                // update this station
                mCurrentStation = newStation
            }
            // check if the station currently used by player service has been changed
            if (mPlayerServiceStation != null && newStation != null &&
                mPlayerServiceStation!!.streamUri.equals(newStation.streamUri)
            ) { // stop sleep timer - if necessary
                // update station currently used by player service
                mPlayerServiceStation = newStation
            }
        }
    }

    /* Initializes and registers broadcast receivers */
    private fun initializeBroadcastReceivers() { // RECEIVER: state of playback has changed
        mPlaybackStateChangedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.hasExtra(EXTRA_STATION)) {
                    handlePlaybackStateChange(intent)
                } else if (intent.hasExtra(EXTRA_ERROR_OCCURRED) && intent.getBooleanExtra(
                        EXTRA_ERROR_OCCURRED,
                        false
                    )
                ) {
                    handlePlaybackStateError(intent)
                }
            }
        }
        val playbackStateChangedIntentFilter =
            IntentFilter(ACTION_PLAYBACK_STATE_CHANGED)
        LocalBroadcastManager.getInstance(mActivity)
            .registerReceiver(mPlaybackStateChangedReceiver!!, playbackStateChangedIntentFilter)
        // RECEIVER: station metadata has changed
        mMetadataChangedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.hasExtra(EXTRA_STATION)) {
                    handleMetadataChange(intent)
                }
            }
        }
        val metadataChangedIntentFilter = IntentFilter(ACTION_METADATA_CHANGED)
        LocalBroadcastManager.getInstance(mActivity)
            .registerReceiver(mMetadataChangedReceiver!!, metadataChangedIntentFilter)
    }


    /* Unregisters broadcast receivers */
    fun unregisterBroadcastReceivers(context: Context?) {
        LocalBroadcastManager.getInstance(context!!)
            .unregisterReceiver(mPlaybackStateChangedReceiver!!)
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMetadataChangedReceiver!!)
        LogHelper.v(LOG_TAG, "Unregistered broadcast receivers in adapter")
    }

    /* handles changes in metadata */
    private fun handleMetadataChange(intent: Intent) { // get new station from intent
        val station: Station
        station = if (intent.hasExtra(EXTRA_STATION)) {
            intent.getParcelableExtra(EXTRA_STATION)
        } else {
            return
        }
        // create copies of station and of main list of stations
        val newStation = Station(station)
        // update liva data station from PlayerService - used in PlayerFragment
        mCollectionViewModel!!.playerServiceStation.setValue(newStation)
    }


    /* handles changes in playback state */
    private fun handlePlaybackStateChange(intent: Intent) { // get new station from intent
        val station: Station
        station = if (intent.hasExtra(EXTRA_STATION)) {
            intent.getParcelableExtra(EXTRA_STATION)
        } else {
            return
        }
        // create copies of station and of main list of stations
        val newStation = Station(station)
        // try to set playback state of previous station

        // update liva data station from PlayerService - used in PlayerFragment
        mCollectionViewModel!!.playerServiceStation.value = newStation
    }

    /* Handles a playback state error that can occur when Transistor crashes during playback */
    private fun handlePlaybackStateError(intent: Intent) {
        LogHelper.e(LOG_TAG,"Forcing a reload of station list. Did Transistor crash?")
    }
}