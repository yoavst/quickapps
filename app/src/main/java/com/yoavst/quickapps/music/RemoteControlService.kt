package com.yoavst.quickapps.music

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.RemoteController
import android.media.RemoteController.MetadataEditor
import android.view.KeyEvent

import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.properties.Delegates
import android.annotation.TargetApi
import android.os.Build
import com.yoavst.kotlin.audioManager

TargetApi(Build.VERSION_CODES.KITKAT)
public class RemoteControlService : AbstractRemoteControlService(), RemoteController.OnClientUpdateListener {
    private var remoteController: RemoteController by Delegates.notNull()
    private var context: Context by Delegates.notNull()
    private var pendingIntentField: Field? = null

    //external callback provided by user.
    private var externalClientUpdateListener: RemoteController.OnClientUpdateListener? = null

    override fun onCreate() {
        //saving the context for further reuse
        context = getApplicationContext()
        remoteController = RemoteController(context, this)
    }

    override fun onDestroy() {
        setRemoteControllerDisabled()
    }
    //Following method will be called by Activity using IBinder

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
     * Sets up external callback for client update events.
     *
     * @param listener External callback.
     */
    public fun setClientUpdateListener(listener: RemoteController.OnClientUpdateListener) {
        externalClientUpdateListener = listener
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

    /**
     * @return Current song position in milliseconds.
     */
    public fun getEstimatedPosition(): Long {
        return remoteController.getEstimatedMediaPosition()
    }


    //end of Binder methods.
    //helper methods

    //this method let us avoid the bug in RemoteController
    //which results in Exception when calling RemoteController#setSynchronizationMode(int)
    //doesn't seem to work though
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

    }

    private fun sendKeyEvent(keyCode: Int): Boolean {
        //send "down" and "up" keyevents.
        var keyEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        val first = remoteController.sendMediaKeyEvent(keyEvent)
        keyEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)
        val second = remoteController.sendMediaKeyEvent(keyEvent)
        return first && second //if both  clicks were delivered successfully
    }
    //end of helper methods.

    override fun getCurrentClientIntent(): Intent? {
        val clientIntent: PendingIntent?
        try {
            clientIntent = pendingIntentField!!.get(remoteController) as PendingIntent
            if (clientIntent == null) return null
            val packageName = clientIntent.getCreatorPackage()
            if (packageName == null) return null
            val result = getPackageManager().getLaunchIntentForPackage(packageName)
            if (result == null) return null
            result.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return result
        } catch (exception: Exception) {
            return null
        }

    }

    //implementation of RemoteController.OnClientUpdateListener. Does nothing other than calling external callback.
    override fun onClientChange(arg0: Boolean) {
        if (externalClientUpdateListener != null) {
            externalClientUpdateListener!!.onClientChange(arg0)
        }
    }

    override fun onClientMetadataUpdate(arg0: MetadataEditor) {
        if (externalClientUpdateListener != null) {
            externalClientUpdateListener!!.onClientMetadataUpdate(arg0)
        }
    }

    override fun onClientPlaybackStateUpdate(arg0: Int) {
        if (externalClientUpdateListener != null) {
            externalClientUpdateListener!!.onClientPlaybackStateUpdate(arg0)
        }
    }

    override fun onClientPlaybackStateUpdate(arg0: Int, arg1: Long, arg2: Long, arg3: Float) {
        if (externalClientUpdateListener != null) {
            externalClientUpdateListener!!.onClientPlaybackStateUpdate(arg0, arg1, arg2, arg3)
        }
    }

    override fun onClientTransportControlUpdate(arg0: Int) {
        if (externalClientUpdateListener != null) {
            externalClientUpdateListener!!.onClientTransportControlUpdate(arg0)
        }

    }

}