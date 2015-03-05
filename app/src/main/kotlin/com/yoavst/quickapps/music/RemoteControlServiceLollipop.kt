package com.yoavst.quickapps.music

import android.annotation.TargetApi
import android.content.ComponentName
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import android.os.Bundle

import java.util.ArrayList
import android.content.Context
import com.mobsandgeeks.ake.mediaSessionManager

TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class RemoteControlServiceLollipop : AbstractRemoteControlService(), MediaSessionManager.OnActiveSessionsChangedListener {
    var controllers: ArrayList<MediaController>? = null
    var listener: MediaControllerListener? = null
    override fun getCurrentClientIntent(): Intent? {
        if (controllers != null) {
            if (listener != null) {
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

    fun notifyListener() {
        if (listener != null && controllers != null && controllers!!.size() != 0) {
            for (controller in controllers!!) {
                if (controller.getPlaybackState() != null && controller.getPlaybackState().getState() == PlaybackState.STATE_PLAYING) {
                    listener!!.onMetadataChanged(controller.getMetadata())
                    return
                }
            }
            for (controller in controllers!!) {
                if (controller.getPlaybackState() != null && controller.getPlaybackState().getState() == PlaybackState.STATE_PAUSED) {
                    listener!!.onMetadataChanged(controller.getMetadata())
                    listener!!.onPlaybackStateChanged(controller.getPlaybackState())
                    return
                }
            }
        }
    }

    override fun onActiveSessionsChanged(mediaControllers: List<MediaController>) {
        if (controllers != null && controllers!!.size() > 0) {
            for (controller in controllers!!) {
                controller.unregisterCallback(callback)
            }
        }
        this.controllers = ArrayList(mediaControllers)
        for (controller in controllers!!) {
            controller.registerCallback(callback)
        }
        notifyListener()
    }

    var callback: MediaController.Callback = object : MediaController.Callback() {
        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            listener?.onSessionDestroyed()
        }

        override fun onSessionEvent(event: String, extras: Bundle) {
            super.onSessionEvent(event, extras)
            listener?.onSessionEvent(event, extras)
        }

        override fun onPlaybackStateChanged(state: PlaybackState) {
            super.onPlaybackStateChanged(state)
            listener?.onPlaybackStateChanged(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadata?) {
            super.onMetadataChanged(metadata)
            listener?.onMetadataChanged(metadata)
        }

        override fun onQueueChanged(queue: List<MediaSession.QueueItem>) {
            super.onQueueChanged(queue)
            listener?.onQueueChanged(queue)
        }

        override fun onQueueTitleChanged(title: CharSequence) {
            super.onQueueTitleChanged(title)
            listener?.onQueueTitleChanged(title)
        }

        override fun onExtrasChanged(extras: Bundle) {
            super.onExtrasChanged(extras)
            listener?.onExtrasChanged(extras)
        }

        override fun onAudioInfoChanged(info: MediaController.PlaybackInfo) {
            super.onAudioInfoChanged(info)
            listener?.onAudioInfoChanged(info)
        }
    }

    /**
     * Disables RemoteController.
     */
    override fun setRemoteControllerDisabled() {
        listener = null
        val manager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        manager.removeOnActiveSessionsChangedListener(this)
        if (controllers != null) {
            for (controller in controllers!!) {
                controller.unregisterCallback(callback)
            }
        }
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

    /**
     * Sets up external callback for client update events.
     *
     * @param listener External callback.
     */
    public fun setClientUpdateListener(listener: MediaControllerListener) {
        this.listener = listener
        notifyListener()
    }


    //endregion

    public trait MediaControllerListener {
        /**
         * Override to handle the session being destroyed. The session is no
         * longer valid after this call and calls to it will be ignored.
         */
        public fun onSessionDestroyed()

        /**
         * Override to handle custom events sent by the session owner without a
         * specified interface. Controllers should only handle these for
         * sessions they own.
         *
         * @param event  The event from the session.
         * @param extras Optional parameters for the event, may be null.
         */
        public fun onSessionEvent(event: String, extras: Bundle?)

        /**
         * Override to handle changes in playback state.
         *
         * @param state The new playback state of the session
         */
        public fun onPlaybackStateChanged(state: PlaybackState)

        /**
         * Override to handle changes to the current metadata.
         *
         * @param metadata The current metadata for the session or null if none.
         * @see android.media.MediaMetadata
         */
        public fun onMetadataChanged(metadata: MediaMetadata?)

        /**
         * Override to handle changes to items in the queue.
         *
         * @param queue A list of items in the current play queue. It should
         *              include the currently playing item as well as previous and
         *              upcoming items if applicable.
         * @see android.media.session.MediaSession.QueueItem
         */
        public fun onQueueChanged(queue: List<MediaSession.QueueItem>?)

        /**
         * Override to handle changes to the queue title.
         *
         * @param title The title that should be displayed along with the play queue such as
         *              "Now Playing". May be null if there is no such title.
         */
        public fun onQueueTitleChanged(title: CharSequence?)

        /**
         * Override to handle changes to the {@link android.media.session.MediaSession} extras.
         *
         * @param extras The extras that can include other information associated with the
         *               {@link android.media.session.MediaSession}.
         */
        public fun onExtrasChanged(extras: Bundle?)

        /**
         * Override to handle changes to the audio info.
         *
         * @param info The current audio info for this session.
         */
        public fun onAudioInfoChanged(info: MediaController.PlaybackInfo)
    }
}