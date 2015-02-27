package com.yoavst.quickapps.desktop.modules

import android.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.yoavst.quickapps.R
import android.content.Intent

/**
 * Created by Yoav.
 */
public class MusicFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.desktop_module_music, container, false);
        view.findViewById(R.id.listener_row).setOnClickListener { v -> getActivity().startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) };
        return view

    }
}