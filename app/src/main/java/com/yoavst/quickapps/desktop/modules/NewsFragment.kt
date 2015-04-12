package com.yoavst.quickapps.desktop.modules

import android.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.yoavst.quickapps.R

import com.yoavst.quickapps.NewsPrefManager
import com.yoavst.kotlin.toast

/**
 * Created by Yoav.
 */
public class NewsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.desktop_module_news, container, false)
        view.findViewById(R.id.logout_row).setOnClickListener { v ->
            NewsPrefManager(getActivity()).clear().apply();
            toast(R.string.news_logout_feedly)
        }
        return view
    }
}