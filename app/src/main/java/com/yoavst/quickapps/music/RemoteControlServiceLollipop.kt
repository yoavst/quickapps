package com.yoavst.quickapps.music

import android.annotation.TargetApi
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import android.os.Bundle
import com.yoavst.kotlin.e
import com.yoavst.kotlin.mediaSessionManager
import com.yoavst.quickapps.R
import java.util.ArrayList

TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class RemoteControlServiceLollipop : AbstractRemoteControlService(), MediaSessionManager.OnActiveSessionsChangedListener {
    var controllers: ArrayList<MediaController>? = null

    override fun getCurrentClientIntent(): Intent? {
        if (controllers != null) {
            if (callback != null) {
                for (controller in controllers!!) {
                    if (controller.getPlaybackState() != null && controller.getPlaybackState().getState() == PlaybackState.STATE_PLAYING) {
                        return getPackageManager().getLaunchIntentForPackage(controller.getPackageName())
                    }
                }
                for (controller in controllers!!) {
                    if (controller.getPlaybackState() != null && controller.getPlaybackState().getState() == PlaybackState.STATE_PAUSED) {
                        return getPackageManager().getLaunchIntentForPackage(controller.getPackageName())
                    }
                }
            }
        }
        return null
    }

    override fun setListener(callback: AbstractRemoteControlService.Callback?) {
        super<AbstractRemoteControlService>.setListener(callback)
        notifyCallback()
    }

    override fun isPlaying(): Boolean {
        if (controllers == null) return false
        for (controller in controllers!!) {
            if (controller.getPlaybackState() != null && controller.getPlaybackState().getState() == PlaybackState.STATE_PLAYING) {
                return true
            }
        }
        return false
    }

    /**
     * Enables the RemoteController thus allowing us to receive metadata updates.
     *
     * @return true if registered successfully
     */
    override fun setRemoteControllerEnabled(): Boolean {
        try {
            val manager = mediaSessionManager()
            manager!!.addOnActiveSessionsChangedListener(this, ComponentName(this, javaClass))
            onActiveSessionsChanged(manager.getActiveSessions(ComponentName(this, javaClass)))
            return true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Disables RemoteController.
     */
    override fun setRemoteControllerDisabled() {
        callback = null
        val manager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        manager.removeOnActiveSessionsChangedListener(this)
        if (controllers != null) {
            for (controller in controllers!!) {
                controller.unregisterCallback(mediaControllerCallback)
            }
        }
    }

    override fun getPosition(): Int {
        for (controller in controllers!!) {
            if (controller.getPlaybackState() != null && controller.getPlaybackState().getState() == PlaybackState.STATE_PLAYING) {
                return millisToSeconds(controller.getPlaybackState().getPosition())
            }
        }
        for (controller in controllers!!) {
            if (controller.getPlaybackState() != null && controller.getPlaybackState().getState() == PlaybackState.STATE_PAUSED) {
                return millisToSeconds(controller.getPlaybackState().getPosition())
            }
        }
        return -1
    }

    //region KeyEvents

    /**
     * Sends "next" media key press.
     */
    override fun sendNextKey() {
        if (controllers != null) {
            for (controller in controllers!!) {
                if (controller.getPlaybackState() != null && controller.getPlaybackState().getState() == PlaybackState.STATE_PLAYING) {
                    val controls = controller.getTransportControls()
                    controls.skipToNext()
                }
            }
        }
    }

    /**
     * Sends "previous" media key press.
     */
    override fun sendPreviousKey() {
        if (controllers != null) {
            for (controller in controllers!!) {
                if (controller.getPlaybackState() != null && controller.getPlaybackState().getState() == PlaybackState.STATE_PLAYING) {
                    val controls = controller.getTransportControls()
                    controls.skipToPrevious()
                }
            }
        }
    }

    /**
     * Sends "pause" media key press, or, if player ignored this button, "play/pause".
     */

    override fun sendPauseKey() {
        if (controllers != null) {
            for (controller in controllers!!) {
                if (controller.getPlaybackState() != null && controller.getPlaybackState().getState() == PlaybackState.STATE_PLAYING) {
                    val controls = controller.getTransportControls()
                    controls.pause()
                }
            }
        }
    }

    /**
     * Sends "play" button press, or, if player ignored it, "play/pause".
     */
    override fun sendPlayKey() {
        if (controllers != null) {
            for (controller in controllers!!) {
                if (controller.getPlaybackState() != null && controller.getPlaybackState().getState() == PlaybackState.STATE_PAUSED) {
                    val controls = controller.getTransportControls()
                    controls.play()
                    return
                }
            }
        }
    }
    //endregion

    override fun onActiveSessionsChanged(mediaControllers: List<MediaController>) {
        if (controllers != null && controllers!!.size() > 0) {
            for (controller in controllers!!) {
                controller.unregisterCallback(mediaControllerCallback)
            }
        }
        this.controllers = ArrayList(mediaControllers)
        for (controller in controllers!!) {
            controller.registerCallback(mediaControllerCallback)
        }
        notifyCallback()
    }

    fun notifyCallback() {
        if (callback != null && controllers != null && controllers!!.size() != 0) {
            for (controller in controllers!!) {
                if (controller.getPlaybackState() != null && controller.getPlaybackState().getState() == PlaybackState.STATE_PLAYING) {
                    notifyCallbackMetadata(controller.getMetadata())
                    callback!!.onPlaybackStateChanged(controller.getPlaybackState().getState())
                    return
                }
            }
            for (controller in controllers!!) {
                if (controller.getPlaybackState() != null && controller.getPlaybackState().getState() == PlaybackState.STATE_PAUSED) {
                    notifyCallbackMetadata(controller.getMetadata())
                    callback!!.onPlaybackStateChanged(controller.getPlaybackState().getState())
                    return
                }
            }
        }
    }

    fun notifyCallbackMetadata(metadata: MediaMetadata?) {
        if (metadata != null) {
            var artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
            if (artist == null)
                artist = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
            if (artist == null) artist = getString(R.string.unknown)
            val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
            var bitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ART)
            if (bitmap == null)
                bitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
            val duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)
            callback?.onMediaMetadataChanged(artist, title, millisToSeconds(duration), bitmap)
        } else callback?.onClientChange(true)
    }

    var mediaControllerCallback: MediaController.Callback = object : MediaController.Callback() {
        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            callback?.onClientChange(true)
        }

        override fun onSessionEvent(event: String, extras: Bundle) {
            super.onSessionEvent(event, extras)
        }

        override fun onPlaybackStateChanged(state: PlaybackState) {
            super.onPlaybackStateChanged(state)
            callback?.onPlaybackStateChanged(state.getState())
        }

        override fun onMetadataChanged(metadata: MediaMetadata?) {
            super.onMetadataChanged(metadata)
            if (metadata != null)
                notifyCallbackMetadata(metadata)
        }

        override fun onQueueChanged(queue: List<MediaSession.QueueItem>) {
            super.onQueueChanged(queue)
        }

        override fun onQueueTitleChanged(title: CharSequence) {
            super.onQueueTitleChanged(title)
        }

        override fun onExtrasChanged(extras: Bundle) {
            super.onExtrasChanged(extras)
        }

        override fun onAudioInfoChanged(info: MediaController.PlaybackInfo) {
            super.onAudioInfoChanged(info)
        }
    }


}