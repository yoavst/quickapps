package com.yoavst.quickapps.desktop.modules

import android.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.yoavst.quickapps.R

/**
 * Created by Yoav.
 */
public open class EmptyFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.desktop_module_empty, container, false)
}