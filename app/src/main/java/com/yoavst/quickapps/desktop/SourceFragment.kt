package com.yoavst.quickapps.desktop

import android.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.malinskiy.materialicons.IconDrawable
import com.malinskiy.materialicons.Iconify
import com.mikepenz.aboutlibraries.Libs
import com.yoavst.kotlin.colorResource
import com.yoavst.quickapps.R
import com.yoavst.util.colorize
import com.yoavst.util.setBigger
import java.util.Locale
import kotlinx.android.synthetic.desktop_fragment_source.*
/**
 * Created by Yoav.
 */
public class SourceFragment : Fragment() {
    val blueColor: Int by colorResource(R.color.primary_color)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.desktop_fragment_source, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Locale.getDefault().getLanguage().startsWith("en")) {
            val text1 = SpannableString(getString(R.string.source_description))
            text1.setBigger(1.5F, 15, 32).colorize(blueColor, 15, 32)
            text1.setBigger(1.5F, 46, 52).colorize(blueColor, 46, 52)
            text1.setBigger(1.5F, 67, 73).colorize(blueColor, 67, 73)
            sourceText.setText(text1)

        }
        circularButton.setImageDrawable(IconDrawable(getActivity(), Iconify.IconValue.md_public).sizeDp(32).color(Color.WHITE))
        circularButton.setOnClickListener {
            getActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/yoavst/quickapps")))
        }
        val bundle = Bundle()
        bundle.putBoolean(Libs.BUNDLE_VERSION, true)
        bundle.putBoolean(Libs.BUNDLE_LICENSE, true)
        bundle.putBoolean(Libs.BUNDLE_AUTODETECT, false)
        bundle.putStringArray(Libs.BUNDLE_FIELDS, Libs.toStringArray(javaClass<R.string>().getFields()))
        aboutLibsView.configureLibraries(bundle)

    }
}