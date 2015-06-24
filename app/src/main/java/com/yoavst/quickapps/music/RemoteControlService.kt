package com.yoavst.quickapps.music

import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaMetadataEditor
import android.media.MediaMetadataRetriever
import android.media.RemoteController
import android.os.Build
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import com.yoavst.kotlin.audioManager
import com.yoavst.quickapps.R
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.properties.Delegates

TargetApi(Build.VERSION_CODES.KITKAT)
SuppressWarnings("deprecation")
public class RemoteControlService : AbstractRemoteControlService(), RemoteController.OnClientUpdateListener {
    private var remoteController: RemoteController by Delegates.notNull()
    private var context: Context by Delegates.notNull()
    private var pendingIntentField: Field? = null
    private var fieldPlaybackState: Field? = null


    override fun onCreate() {
        //saving the context for further reuse
        context = getApplicationContext()
        remoteController = RemoteController(context, this)
    }

    override fun onDestroy() {
        setRemoteControllerDisabled()
    }

    /**
     * Enables the RemoteController thus allowing us to receive metadata updates.
     *
     * @return true if registered successfully
     */
    override fun setRemoteControllerEnabled(): Boolean {
        if (!(context.audioManager()).registerRemoteController(remoteController)) {
            return false
        } else {
            remoteController.setArtworkConfiguration(AbstractRemoteControlService.BITMAP_WIDTH, AbstractRemoteControlService.BITMAP_HEIGHT)
            setSynchronizationMode(remoteController, RemoteController.POSITION_SYNCHRONIZATION_CHECK)
            return true
        }
    }

    /**
     * Disables RemoteController.
     */
    override fun setRemoteControllerDisabled() {
        (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).unregisterRemoteController(remoteController)
    }

    /**
     * Sends "next" media key press.
     */
    override fun sendNextKey() {
        sendKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT)
    }

    /**
     * Sends "previous" media key press.
     */
    override fun sendPreviousKey() {
        sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
    }

    /**
     * Sends "pause" media key press, or, if player ignored this button, "play/pause".
     */
    override fun sendPauseKey() {
        if (!sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PAUSE)) {
            sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
        }
    }

    /**
     * Sends "play" button press, or, if player ignored it, "play/pause".
     */
    override fun sendPlayKey() {
        if (!sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY)) {
            sendKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
        }
    }

    private fun sendKeyEvent(keyCode: Int): Boolean {
        //send "down" and "up" key events.
        var keyEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        val first = remoteController.sendMediaKeyEvent(keyEvent)
        keyEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)
        val second = remoteController.sendMediaKeyEvent(keyEvent)
        return first && second //if both clicks were delivered successfully
    }

    override fun getCurrentClientIntent(): Intent? {
        val clientIntent: PendingIntent?
        try {
            clientIntent = pendingIntentField!!.get(remoteController) as? PendingIntent ?: return null
            val packageName = clientIntent.getCreatorPackage() ?: return null
            val result = getPackageManager().getLaunchIntentForPackage(packageName) ?: return null
            result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return result
        } catch (exception: Exception) {
            return null
        }

    }

    override fun isPlaying(): Boolean {
        try {
            return (fieldPlaybackState!!.get(remoteController) as? Int ?: -1) == PlaybackStateCompat.STATE_PLAYING
        } catch (exception: Exception) {
            return false
        }
    }

    override fun getPosition(): Int = millisToSeconds(remoteController.getEstimatedMediaPosition())

    override fun onClientChange(clearing: Boolean) {
        callback?.onClientChange(clearing)
    }

    override fun onClientMetadataUpdate(metadataEditor: RemoteController.MetadataEditor) {
        val artist = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_ARTIST,
                metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, getString(R.string.unknown)))
        val title = metadataEditor.getString(MediaMetadataRetriever.METADATA_KEY_TITLE, getString(R.string.unknown))
        val bitmap = metadataEditor.getBitmap(MediaMetadataEditor.BITMAP_KEY_ARTWORK, null)
        val duration = metadataEditor.getLong(MediaMetadataRetriever.METADATA_KEY_DURATION, -1)
        callback?.onMediaMetadataChanged(artist, title, millisToSeconds(duration), bitmap)

    }

    override fun onClientPlaybackStateUpdate(state: Int) {
        callback?.onPlaybackStateChanged(state)
    }

    override fun onClientPlaybackStateUpdate(state: Int, stateChangeTimeMs: Long, currentPosMs: Long, speed: Float) {
        callback?.onPlaybackStateChanged(state)
    }

    override fun onClientTransportControlUpdate(transportControlFlags: Int) {
    }

    //region helper methods
    /**
     * this method let us avoid the bug in RemoteController
     * which results in Exception when calling RemoteController#setSynchronizationMode(int)
     */
    private fun setSynchronizationMode(controller: RemoteController, sync: Int) {
        if ((sync != RemoteController.POSITION_SYNCHRONIZATION_NONE) && (sync != RemoteController.POSITION_SYNCHRONIZATION_CHECK)) {
            throw IllegalArgumentException("Unknown synchronization mode " + sync)
        }
        val iRemoteControlDisplayClass: Class<*>
        try {
            iRemoteControlDisplayClass = Class.forName("android.media.IRemoteControlDisplay")
        } catch (e1: ClassNotFoundException) {
            throw RuntimeException("Class IRemoteControlDisplay doesn't exist, can't access it with reflection")
        }

        val remoteControlDisplayWantsPlaybackPositionSyncMethod: Method
        try {
            remoteControlDisplayWantsPlaybackPositionSyncMethod = javaClass<AudioManager>().getDeclaredMethod("remoteControlDisplayWantsPlaybackPositionSync", iRemoteControlDisplayClass, java.lang.Boolean.TYPE)
            remoteControlDisplayWantsPlaybackPositionSyncMethod.setAccessible(true)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Method remoteControlDisplayWantsPlaybackPositionSync() doesn't exist, can't access it with reflection")
        }

        val rcDisplay: Any
        val rcDisplayField: Field
        try {
            rcDisplayField = javaClass<RemoteController>().getDeclaredField("mRcd")
            rcDisplayField.setAccessible(true)
            rcDisplay = rcDisplayField.get(remoteController)
        } catch (e: NoSuchFieldException) {
            throw RuntimeException("Field mRcd doesn't exist, can't access it with reflection")
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Field mRcd can't be accessed - access denied")
        } catch (e: IllegalArgumentException) {
            throw RuntimeException("Field mRcd can't be accessed - invalid argument")
        }

        val am = context.audioManager()
        try {
            remoteControlDisplayWantsPlaybackPositionSyncMethod.invoke(am, iRemoteControlDisplayClass.cast(rcDisplay), true)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Method remoteControlDisplayWantsPlaybackPositionSync() invocation failure - access denied")
        } catch (e: IllegalArgumentException) {
            throw RuntimeException("Method remoteControlDisplayWantsPlaybackPositionSync() invocation failure - invalid arguments")
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Method remoteControlDisplayWantsPlaybackPositionSync() invocation failure - invalid invocation target")
        }

        try {
            pendingIntentField = javaClass<RemoteController>().getDeclaredField("mClientPendingIntentCurrent")
            pendingIntentField!!.setAccessible(true)
        } catch (e: NoSuchFieldException) {
            // Do nothing
        }

        try {
            fieldPlaybackState = javaClass<RemoteController>().getDeclaredField("mPlaybackState")
            fieldPlaybackState!!.setAccessible(true)
        } catch (e: NoSuchFieldException) {
            // Do nothing
        }

    }
    //endregion
}