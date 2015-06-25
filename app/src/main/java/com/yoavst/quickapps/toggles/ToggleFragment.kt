package com.yoavst.quickapps.toggles

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import kotlinx.android.synthetic.toggles_toggle_fragment.image
import kotlinx.android.synthetic.toggles_toggle_fragment.title

/**
 * Created by Yoav.
 */
public abstract class ToggleFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val relativeLayout = inflater.inflate(R.layout.toggles_toggle_fragment, container, false) as RelativeLayout
        relativeLayout.setOnTouchListener { v, e -> (getActivity() as QCircleActivity).gestureDetector.onTouchEvent(e) }
        return relativeLayout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image.setOnClickListener { onToggleButtonClicked() }
        title.setText(getTitle())
        init()
    }

    public abstract fun onToggleButtonClicked()

    public abstract fun getIntentForLaunch(): Intent

    public abstract fun init()


    public abstract fun getTitle(): CharSequence
}
