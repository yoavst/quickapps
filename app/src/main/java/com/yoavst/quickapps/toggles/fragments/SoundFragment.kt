package com.yoavst.quickapps.toggles.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import com.yoavst.kotlin.beforeLollipop

import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.CTogglesActivity
import com.yoavst.quickapps.toggles.ToggleFragment
import kotlin.properties.Delegates
import com.yoavst.kotlin.stringResource
import com.yoavst.kotlin.systemService

/**
 * Created by Yoav.
 */
public class SoundFragment : ToggleFragment() {
    val SOUND by stringResource(R.string.sound_sound)
    val SILENT by stringResource(R.string.sound_silent)
    val VIBRATE by stringResource(R.string.sound_vibrate)
    val audioManager: AudioManager by systemService()
    var mRingerReceiver: BroadcastReceiver? = null
    val systemUiResources: Resources by Delegates.lazy { (getActivity() as CTogglesActivity).getSystemUiResource() }

    override fun init() {
        toggleTitle.setText(SOUND)
        if (mRingerReceiver == null) {
            mRingerReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    setToggleData()
                }
            }
        }
        if (soundIcon == -1 || vibrateIcon == -1 || silentIcon == -1) {
            soundIcon = systemUiResources.getIdentifier("indi_noti_sound_on", "drawable", "com.android.systemui")
            vibrateIcon = systemUiResources.getIdentifier("indi_noti_sound_vib_on", "drawable", "com.android.systemui")
            silentIcon = systemUiResources.getIdentifier("indi_noti_silent_on", "drawable", "com.android.systemui")
        }
        val filter = IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION)
        getActivity().registerReceiver(mRingerReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            getActivity().unregisterReceiver(mRingerReceiver)
        } catch (ignored: Exception) {
            // Do nothing - receiver not registered
        }

    }

    public fun setToggleData() {
        when (audioManager.getRingerMode()) {
            AudioManager.RINGER_MODE_NORMAL -> {
                toggleIcon.setImageDrawable(systemUiResources.getDrawable(soundIcon))
                toggleText.setText(SOUND)
            }
            AudioManager.RINGER_MODE_VIBRATE -> {
                toggleIcon.setImageDrawable(systemUiResources.getDrawable(vibrateIcon))
                toggleText.setText(VIBRATE)
            }
            AudioManager.RINGER_MODE_SILENT -> {
                toggleIcon.setImageDrawable(systemUiResources.getDrawable(silentIcon))
                toggleText.setText(SILENT)
            }
        }
    }

    override fun onToggleButtonClicked() {
        when (audioManager.getRingerMode()) {
            AudioManager.RINGER_MODE_NORMAL -> audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE)
            AudioManager.RINGER_MODE_VIBRATE -> if (beforeLollipop())
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT)
            else
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
            AudioManager.RINGER_MODE_SILENT -> audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
        }
    }

    override fun getIntentForLaunch(): Intent {
        return Intent(Settings.ACTION_SOUND_SETTINGS)
    }

    class object {
        // resources id of system ui stuff
        var soundIcon = -1
        var vibrateIcon = -1
        var silentIcon = -1
    }
}
