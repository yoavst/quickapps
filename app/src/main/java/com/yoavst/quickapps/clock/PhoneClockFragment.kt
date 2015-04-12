package com.yoavst.quickapps.clock

import kotlin.properties.Delegates
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.yoavst.quickapps.R
import com.lge.app.floating.FloatableActivity
import android.widget.TextView
import android.widget.Button
import android.widget.LinearLayout
import kotlinx.android.synthetic.stopwatch_qslide_layout.*

/**
 * Created by Yoav.
 */
public class PhoneClockFragment : ClockFragment() {
    var container: ViewGroup? = null
    var layout: View by Delegates.notNull()
    override val time: TextView by Delegates.lazy { layout.findViewById(R.id.time) as TextView }
    override val start: Button by Delegates.lazy { layout.findViewById(R.id.start) as Button }
    override val pause: Button by Delegates.lazy { layout.findViewById(R.id.pause) as Button }
    override val running: LinearLayout by Delegates.lazy { layout.findViewById(R.id.running) as LinearLayout }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.container = container
        this.layout = inflater.inflate(R.layout.stopwatch_qslide_layout, container, false)
        return layout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
       PAUSE.length()
        RESUME.length()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (getActivity() is FloatableActivity) {
            container?.addView(layout)
            super.onViewCreated(getView(), null)
        }
    }
}