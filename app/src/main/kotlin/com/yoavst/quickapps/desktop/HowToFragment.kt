package com.yoavst.quickapps.desktop

import android.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.yoavst.quickapps.R
import at.markushi.ui.CircleButton
import com.malinskiy.materialicons.Iconify
import com.malinskiy.materialicons.IconDrawable
import android.graphics.Color
import android.content.Intent
import android.content.ComponentName
import com.yoavst.util.createExplicit
import com.mobsandgeeks.ake.viewById

/**
 * Created by Yoav.
 */
public class HowToFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.desktop_fragment_howto, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button: CircleButton = viewById(R.id.settings_btn);
        button.setImageDrawable(IconDrawable(getActivity(), Iconify.IconValue.md_settings).sizeDp(32).color(Color.WHITE));
        button.setOnClickListener {
            startActivity(Intent("android.intent.action.MAIN").setComponent(ComponentName("com.android.settings", "com.android.settings.lge.QuickWindowCase")));
        }
    }
}