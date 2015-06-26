package com.yoavst.quickapps.toggles.toggles

import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.provider.Settings
import com.yoavst.kotlin.broadcastReceiver
import com.yoavst.kotlin.drawableResource
import com.yoavst.kotlin.stringResource
import com.yoavst.kotlin.systemService
import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.ToggleFragment
import kotlinx.android.synthetic.toggles_toggle_fragment.*

/**
 * Created by yoavst.
 */
public class SoundFragment : ToggleFragment() {
    val Sound by stringResource(R.string.sound_sound)
    val Vibrate by stringResource(R.string.sound_vibrate)
    val soundDrawable by drawableResource(R.drawable.ic_sound)
    val vibrateDrawable by drawableResource(R.drawable.ic_vibrate)
    val audioManager: AudioManager by systemService()
    val receiver = broadcastReceiver { context, intent ->
        setToggleData()
    }

    override fun onToggleButtonClicked() {
        when (audioManager.getRingerMode()) {
            AudioManager.RINGER_MODE_NORMAL -> audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE)
            AudioManager.RINGER_MODE_VIBRATE, AudioManager.RINGER_MODE_SILENT ->
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
        }
    }

    override fun getIntentForLaunch(): Intent = Intent(Settings.ACTION_SOUND_SETTINGS)

    override fun init() {
        val filter = IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION)
        getActivity().registerReceiver(receiver, filter)
        setToggleData()
        image.setOnLongClickListener {
            audioManager.adjustStreamVolume(3, 0, 1)
            true
        }
    }

    fun setToggleData() {
        when (audioManager.getRingerMode()) {
            AudioManager.RINGER_MODE_NORMAL -> {
                image.setImageDrawable(soundDrawable)
                text.setText(Sound)
                setToggleBackgroundOn()
            }
            AudioManager.RINGER_MODE_VIBRATE, AudioManager.RINGER_MODE_SILENT -> {
                image.setImageDrawable(vibrateDrawable)
                text.setText(Vibrate)
                setToggleBackgroundOff()
            }
        }
    }

    override fun getTitle(): CharSequence = getString(R.string.sound_sound)

    override fun onDestroy() {
        super.onDestroy()
        try {
            getActivity().unregisterReceiver(receiver)
        } catch (ignored: Exception) {
            // Do nothing - receiver not registered
        }

    }
}