package com.yoavst.quickapps.desktop

import android.app.Fragment
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.malinskiy.materialicons.IconDrawable
import com.malinskiy.materialicons.Iconify
import com.yoavst.kotlin.toast
import com.yoavst.quickapps.R
import kotlinx.android.synthetic.desktop_fragment_howto.button
import kotlinx.android.synthetic.desktop_fragment_howto.image

/**
 * Created by yoavst.
 */
public class HowToFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.desktop_fragment_howto, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button.setImageDrawable(IconDrawable(getActivity(), Iconify.IconValue.md_settings).sizeDp(32).color(Color.WHITE))
        button.setOnClickListener {
            try {
                startActivity(Intent("android.intent.action.MAIN").setComponent(ComponentName("com.android.settings", "com.android.settings.lge.QuickWindowCase")))
            } catch (e: Exception) {
                toast(R.string.activity_not_found)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val drawable = (image.getDrawable() as BitmapDrawable)
        image.setImageDrawable(null)
        drawable.getBitmap().recycle()
    }
}