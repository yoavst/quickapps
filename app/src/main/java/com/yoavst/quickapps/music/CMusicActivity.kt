package com.yoavst.quickapps.music

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.support.v4.media.session.PlaybackStateCompat
import com.lge.qcircle.template.QCircleDialog
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import com.yoavst.quickapps.tools.createExplicit
import kotlinx.android.synthetic.music_activity.*
import kotlin.properties.Delegates

public class CMusicActivity : QCircleActivity(), AbstractRemoteControlService.Callback {
    private var remoteControlService: AbstractRemoteControlService by Delegates.notNull()
    private var bound = false
    private var shouldShowRegister = false
    private var lock = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super<QCircleActivity>.onCreate(savedInstanceState)
        setContentView(template.getView())
        if (isServiceEnabled()) {
            setContentViewToMain(R.layout.music_activity)
            template.setBackgroundDrawable(ColorDrawable(colorRes(R.color.md_indigo_A200)))
            playView.setOnClickListener {
                if (bound && !lock) {
                    lock = true
                    if (remoteControlService.isPlaying())
                        remoteControlService.sendPauseKey()
                    else remoteControlService.sendPlayKey()
                }
            }
            back.setOnClickListener { finish() }
            volume.setOnClickListener { audioManager().adjustStreamVolume(3, 0, 1) }
            skipNextLayout.setOnClickListener {
                if (bound) {
                    if (!remoteControlService.isPlaying()) {
                        remoteControlService.sendPlayKey()
                        Handler().postDelayed(500) {
                            remoteControlService.sendNextKey()
                        }
                    } else remoteControlService.sendNextKey()
                }
            }

            skipPrevLayout.setOnClickListener {
                if (bound) {
                    if (!remoteControlService.isPlaying()) {
                        remoteControlService.sendPlayKey()
                        Handler().postDelayed(500) {
                            remoteControlService.sendPreviousKey()
                        }
                    } else remoteControlService.sendPreviousKey()

                }
            }
            title.setSelected(true)
            artist.setSelected(true)
        } else showUnregistered()
    }

    public override fun onStart() {
        super<QCircleActivity>.onStart();
        if (isServiceEnabled()) {
            val intent =
                    if (beforeLollipop())
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
    }

    public override fun onStop() {
        super<QCircleActivity>.onStop()
        if (bound) {
            remoteControlService setListener null
            remoteControlService.setRemoteControllerDisabled()
        }
        try {
            unbindService(serviceConnection)
        } catch (ignored: IllegalArgumentException) {}
    }

    fun showUnregistered() {
        shouldShowRegister = true
        QCircleDialog.Builder()
                .setTitle(getString(R.string.open_the_case))
                .setText(getString(R.string.register_us_please))
                .setMode(QCircleDialog.DialogMode.Error)
                .create().show(this, template)

    }

    override fun getIntentToShow(): Intent? {
        if (shouldShowRegister) {
            return Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        } else {
            try {
                if (bound) return remoteControlService.getCurrentClientIntent()
            } catch(e: Exception) {
            }
            return null
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AbstractRemoteControlService.RCBinder
            remoteControlService = binder.getService()
            if (!remoteControlService.setRemoteControllerEnabled()) {
                // Not registered on the settings
                showUnregistered()
            } else {
                remoteControlService setListener this@CMusicActivity
            }
            bound = true
        }

    }

    override fun onMediaMetadataChanged(artist: String, title: String, duration: Int, albumArt: Bitmap?) {
        this.artist.setText(artist)
        this.title.setText(title)
        if (albumArt != null)
            template.setBackgroundDrawable(BitmapDrawable(getResources(), albumArt))
    }

    override fun onPlaybackStateChanged(state: Int) {
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playView.setPlaying()
        } else {
           playView.setPausing()
        }
        lock = false
    }

    override fun onClientChange(clearing: Boolean) {
        if (clearing) {
            template.setBackgroundColor(colorRes(R.color.md_indigo_A200))
            artist.setText(R.string.unknown)
            title.setText(R.string.unknown)
        }
    }

    fun isServiceEnabled(): Boolean {
        return getPackageName().orEmpty() in Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners")
    }
}