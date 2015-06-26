package com.yoavst.quickapps.news

import android.annotation.SuppressLint
import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yoavst.kotlin.e
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import kotlinx.android.synthetic.news_fragment.source
import kotlinx.android.synthetic.news_fragment.time
import kotlinx.android.synthetic.news_fragment.title
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.properties.Delegates

/**
 * Created by Yoav.
 */
public class NewsFragment : Fragment() {

    val entryNumber by Delegates.lazy { getArguments().getInt(ENTRY_NUMBER) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.news_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        view!!.setOnTouchListener { view, motionEvent -> (getActivity() as QCircleActivity).gestureDetector.onTouchEvent(motionEvent) }
        val entry = (getActivity() as CNewsActivity).getEntry(entryNumber)
        if (entry != null) {
            title.setText(entry.getTitle())
            source.setText(entry.getOrigin().getTitle())
            time.setText(dayFormatter.format(Date(entry.getPublished())))
        }
    }

    companion object {
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