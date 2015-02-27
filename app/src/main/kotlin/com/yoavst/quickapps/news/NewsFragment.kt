package com.yoavst.quickapps.news

import android.annotation.SuppressLint
import android.app.Fragment
import android.widget.TextView

import com.yoavst.quickapps.R

import java.text.SimpleDateFormat
import java.util.Date
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import butterknife.bindView
import kotlin.properties.Delegates
import com.yoavst.quickapps.util.QCircleActivity

/**
 * Created by Yoav.
 */
public class NewsFragment : Fragment() {
    val time: TextView by bindView(R.id.news_time)
    val title: TextView  by bindView(R.id.news_title)
    val source: TextView  by bindView(R.id.news_source)
    val entryNumber by Delegates.lazy { getArguments().getInt(ENTRY_NUMBER) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.news_circle_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        view!!.setOnTouchListener {(view, motionEvent) -> (getActivity() as QCircleActivity).gestureDetector.onTouchEvent(motionEvent) }
        val entry = NewsAdapter.getEntry(entryNumber)
        if (entry != null) {
            title.setText(entry.getTitle())
            source.setText(entry.getOrigin().getTitle())
            time.setText(dayFormatter.format(Date(entry.getPublished())))
        }
    }


    class object {
        SuppressLint("SimpleDateFormat")
        private val dayFormatter = SimpleDateFormat("MMM d, HH:mm")
        private var ENTRY_NUMBER: String = "ENTRY_NUMBER"

        public fun newInstance(entryNumber: Int): NewsFragment {
            var fragment = NewsFragment()
            var args = Bundle()
            args.putInt(ENTRY_NUMBER, entryNumber)
            fragment.setArguments(args)
            return fragment
        }
    }
}