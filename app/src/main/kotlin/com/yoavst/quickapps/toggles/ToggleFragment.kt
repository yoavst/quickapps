package com.yoavst.quickapps.toggles

import android.content.Intent
import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView

import com.yoavst.quickapps.R
import com.yoavst.quickapps.util.QCircleActivity
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public abstract class ToggleFragment : Fragment() {
    protected var toggleIcon: ImageButton by Delegates.notNull()
    protected var toggleText: TextView by Delegates.notNull()
    protected var toggleTitle: TextView by Delegates.notNull()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val relativeLayout = inflater.inflate(R.layout.toggles_circle_fragment, container, false) as RelativeLayout
        toggleIcon = relativeLayout.findViewById(R.id.toggle_icon) as ImageButton
        toggleText = relativeLayout.findViewById(R.id.toggle_text) as TextView
        toggleTitle = relativeLayout.findViewById(R.id.toggle_title) as TextView
        toggleIcon.setOnClickListener { v -> onToggleButtonClicked() }
        relativeLayout.setOnTouchListener { (v,e) -> (getActivity() as QCircleActivity).gestureDetector.onTouchEvent(e)}
        init()
        return relativeLayout
    }

    public abstract fun onToggleButtonClicked()

    public abstract fun getIntentForLaunch(): Intent

    public abstract fun init()
}