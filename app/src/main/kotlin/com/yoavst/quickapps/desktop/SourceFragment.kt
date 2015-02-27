package com.yoavst.quickapps.desktop

import com.yoavst.util.setBigger
import com.yoavst.util.colorize
import android.app.Fragment
import android.widget.TextView
import com.yoavst.quickapps.AboutLibsView
import at.markushi.ui.CircleButton
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.yoavst.quickapps.R
import kotlin.properties.Delegates
import java.util.Locale
import android.text.SpannableString
import android.graphics.Color
import com.malinskiy.materialicons.Iconify
import com.malinskiy.materialicons.IconDrawable
import android.content.Intent
import android.net.Uri
import com.mikepenz.aboutlibraries.Libs
import butterknife.bindView
import com.mobsandgeeks.ake.getColor

/**
 * Created by Yoav.
 */
public class SourceFragment : Fragment() {
    val sourceText: TextView by bindView(R.id.source_text)
    val aboutLibsView: AboutLibsView by bindView(R.id.aboutLibs)
    val circularButton: CircleButton by bindView(R.id.github)
    val blueColor: Int by Delegates.lazy { getColor(R.color.primary_color) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.desktop_fragment_source, container, false)

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