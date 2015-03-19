package com.yoavst.quickapps.music

import com.yoavst.quickapps.util.QCircleActivity
import com.lge.qcircle.template.QCircleTemplate
import android.content.Intent
import kotlin.properties.Delegates
import com.lge.qcircle.template.TemplateType
import android.os.Bundle
import android.widget.TextView
import android.widget.ProgressBar
import android.graphics.Color
import com.lge.qcircle.template.TemplateTag
import android.widget.RelativeLayout
import android.widget.LinearLayout
import com.yoavst.quickapps.R
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.lge.qcircle.template.QCircleBackButton
import android.widget.FrameLayout
import com.mobsandgeeks.ake.audioManager
import com.mobsandgeeks.ake.postDelayed
import android.os.Handler
import com.mobsandgeeks.ake.preLollipop
import android.content.Context
import com.yoavst.util.createExplicit
import android.content.ServiceConnection
import android.content.ComponentName
import android.os.IBinder
import android.annotation.TargetApi
import android.os.Build
import android.media.session.PlaybackState
import android.media.MediaMetadata
import android.media.RemoteControlClient
import android.graphics.drawable.BitmapDrawable
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.RemoteController
import java.util.concurrent.TimeUnit
import com.mobsandgeeks.ake.e
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataEditor

/**
 * Created by Yoav.
 */
public class CMusicActivity : QCircleActivity() {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    private var playPauseButton: TextView by Delegates.notNull()
    private var progressBar: ProgressBar by Delegates.notNull()
    private var artistText: TextView by Delegates.notNull()
    private var titleText: TextView by Delegates.notNull()
    private var rcService: AbstractRemoteControlService by Delegates.notNull()
    private var bound = false
    private var isPlaying = false
    private var shouldShowRegister = false
    private var songDuration: Long = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        template.setBackButton()
        template.setBackButtonTheme(true)
        template.setBackgroundColor(Color.WHITE, true)
        val contentParent = template.getLayoutById(TemplateTag.CONTENT).getParent() as RelativeLayout
        val layoutForButtons = LayoutInflater.from(this).inflate(R.layout.music_circle_button_layout, contentParent, false) as LinearLayout
        val nextButton = layoutForButtons.findViewById(R.id.next) as TextView
        val backButton = layoutForButtons.findViewById(R.id.back) as TextView
        playPauseButton = layoutForButtons.findViewById(R.id.pause_start) as TextView
        fun touchCallback(view: View, event: MotionEvent) = onPlusTouched(view, event)
        nextButton.setOnTouchListener(::touchCallback)
        backButton.setOnTouchListener(::touchCallback)
        playPauseButton.setOnTouchListener(::touchCallback)
        progressBar = layoutForButtons.findViewById(R.id.music_progress) as ProgressBar
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.control_buttons_height))
        params.addRule(RelativeLayout.ABOVE, R.id.backButton)
        layoutForButtons.setLayoutParams(params)
        contentParent.addView(layoutForButtons)
        val main = template.getLayoutById(TemplateTag.CONTENT)
        val headerLayout = LayoutInflater.from(this).inflate(R.layout.music_circle_header_layout, main, false) as FrameLayout
        artistText = headerLayout.findViewById(R.id.artist_text) as TextView
        titleText = headerLayout.findViewById(R.id.title_text) as TextView
        titleText.setSelected(true)
        artistText.setSelected(true)
        headerLayout.findViewById(R.id.volume_control).setOnClickListener{v -> audioManager().adjustStreamVolume(3, 0, 1)}
        main.addView(headerLayout)
        setContentView(template.getView())
        playPauseButton.setOnClickListener{v ->
            if (bound) {
                if (isPlaying)
                    rcService.sendPauseKey()
                else rcService.sendPlayKey()
            }
        }
        backButton.setOnClickListener{v ->
            if (bound) {
                if (!isPlaying) {
                    rcService.sendPlayKey()
                    Handler().postDelayed(500) {
                        rcService.sendPreviousKey()
                    }
                } else rcService.sendPreviousKey()

            }
        }
        nextButton.setOnClickListener{v ->
            if (bound) {
                if (!isPlaying) {
                    rcService.sendPlayKey()
                    Handler().postDelayed(500) {
                        rcService.sendNextKey()
                    }
                } else rcService.sendNextKey()
            }
        }
    }

    public override fun onStart() {
        super.onStart();
        val intent =
        if (preLollipop())
            Intent("com.yoavst.quickmusic.BIND_RC_CONTROL_SERVICE")
        else
            Intent("com.yoavst.quickmusic.BIND_RC_CONTROL_SERVICE_LOLLIPOP").createExplicit(this)
        try {
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } catch (e: RuntimeException) {
            e.printStackTrace()
            showUnregistered()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (bound) {
            rcService.setRemoteControllerDisabled()
        }
        unbindService(serviceConnection)
    }

    fun showUnregistered() {
        shouldShowRegister = true
        artistText.setText(R.string.register_us_please)
        titleText.setText(R.string.open_the_case)
    }

    fun onPlusTouched(view: View, event: MotionEvent): Boolean {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            view as TextView setTextColor (getResources() getColor R.color.md_pink_500)
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            view as TextView setTextColor Color.WHITE
        }
        return false
    }

    override fun getIntentToShow(): Intent? {
        return if (shouldShowRegister) {
            Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        } else {
            if (bound && rcService != null) rcService.getCurrentClientIntent()
            null
        }
    }

    private val serviceConnection = object : ServiceConnection {
        public override fun onServiceConnected(className: ComponentName, service: IBinder) {
            //Getting the binder and activating RemoteController instantly
            val binder = service as AbstractRemoteControlService.RCBinder
            rcService = binder.getService()
            if (!rcService.setRemoteControllerEnabled()) {
                // Not registered on the settings
                showUnregistered()
            }
            if (rcService is RemoteControlService)
                rcService as RemoteControlService setClientUpdateListener listener
            else
                (rcService as RemoteControlServiceLollipop).setClientUpdateListener(object : RemoteControlServiceLollipop.MediaControllerListener {
                    public  override fun onSessionDestroyed() {
                        listener onClientChange true
                    }

                    public override fun onSessionEvent(event: String, extras: Bundle?) {
                    }

                    TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    public override fun onPlaybackStateChanged(state: PlaybackState) {
                        listener onClientPlaybackStateUpdate (if (state.getState() == PlaybackState.STATE_PLAYING) RemoteControlClient.PLAYSTATE_PLAYING else -1)
                    }

                    TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    public override fun onMetadataChanged(metadata: MediaMetadata?) {
                        if (metadata != null) {
                            playPauseButton.setText("{md-pause}")
                            isPlaying = true
                            var artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
                            if (artist == null)
                                artist = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
                            if (artist == null) artist = getString(R.string.unknown)
                            val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
                            songDuration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)
                            artistText.setText(artist)
                            titleText.setText(title)
                            var bitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ART)
                            if (bitmap == null)
                                bitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                            if (bitmap != null) {
                                template.setBackgroundDrawable(BitmapDrawable(getResources(), bitmap), false)
                            } else {
                                template.setBackgroundColor(Color.WHITE, false)
                            }
                        } else {
                            artistText.setText(R.string.unknown)
                            titleText.setText(R.string.unknown)
                            isPlaying = false
                            playPauseButton.setText("{md-play-arrow}")
                            template.setBackgroundColor(Color.WHITE, false)
                        }
                    }

                    public override fun onQueueChanged(queue: List<MediaSession.QueueItem>?) {
                    }

                    public override fun onQueueTitleChanged(title: CharSequence?) {
                    }

                    public override fun onExtrasChanged(extras: Bundle?) {
                    }

                    public override fun onAudioInfoChanged(info: MediaController.PlaybackInfo) {
                    }
                })
            bound = true
        }

        public override fun onServiceDisconnected(name: ComponentName) {
            bound = false
        }
    }


    var listener = object : RemoteController.OnClientUpdateListener {
        public override fun onClientChange(clearing: Boolean) {
            if (clearing) {
                template.setBackgroundColor(Color.WHITE, false)
                artistText.setText(R.string.unknown)
                titleText.setText(R.string.unknown)
            }
        }

        public override fun onClientPlaybackStateUpdate(state: Int) {
            when (state) {
                RemoteControlClient.PLAYSTATE_PLAYING -> {
                    isPlaying = true
                    playPauseButton.setText("{md-pause}")
                }
                else -> {
                    isPlaying = false
                    playPauseButton.setText("{md-play-arrow}")
                }
            }
        }

        public override fun onClientPlaybackStateUpdate(state: Int, stateChangeTimeMs: Long, currentPosMs: Long, speed: Float) {
            progressBar.setProgress(TimeUnit.MILLISECONDS.toSeconds(currentPosMs).toInt())
            when (state) {
                RemoteControlClient.PLAYSTATE_PLAYING -> {
                    isPlaying = true
                    playPauseButton.setText("{md-pause}")
                }
                else -> {
                    isPlaying = false
                    playPauseButton.setText("{md-play-arrow}")
                }
            }
        }

        public override fun onClientTransportControlUpdate(transportControlFlags: Int) {
        }

        public override fun onClientMetadataUpdate(metadataEditor: RemoteController.MetadataEditor) {
            //some players write artist name to METADATA_KEY_ALBUMARTIST instead of METADATA_KEY_ARTIST, so we should double-check it
            val artist = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_ARTIST,
                    metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, getString(R.string.unknown)))
            val title = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_TITLE, getString(R.string.unknown))
            songDuration = metadataEditor.getLong(MediaMetadataRetriever.METADATA_KEY_DURATION, 1)
            artistText.setText(artist)
            titleText.setText(title)
            val bitmap = metadataEditor.getBitmap(MediaMetadataEditor.BITMAP_KEY_ARTWORK, null)
            if (bitmap != null) {
                template.setBackgroundDrawable(BitmapDrawable(getResources(), bitmap), false)
            } else {
                template.setBackgroundColor(Color.WHITE, false)
            }
        }
    }
}