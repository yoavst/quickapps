package com.yoavst.quickapps.desktop

import android.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.malinskiy.materialicons.IconDrawable
import com.malinskiy.materialicons.Iconify
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.ui.adapter.LibsRecyclerViewAdapter
import com.yoavst.kotlin.colorResource
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.colorize
import com.yoavst.quickapps.tools.setBigger
import kotlinx.android.synthetic.desktop_fragment_source.circularButton
import kotlinx.android.synthetic.desktop_fragment_source.recycler
import kotlinx.android.synthetic.desktop_fragment_source.sourceText
import java.util.Locale

public class SourceFragment : Fragment() {
    val fragmentColor: Int by colorResource(R.color.source)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.desktop_fragment_source, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Locale.getDefault().getLanguage().startsWith("en")) {
            val text1 = SpannableString(getString(R.string.source_description))
            text1.setBigger(1.5F, 15, 32).colorize(fragmentColor, 15, 32)
            text1.setBigger(1.5F, 46, 52).colorize(fragmentColor, 46, 52)
            text1.setBigger(1.5F, 67, 73).colorize(fragmentColor, 67, 73)
            sourceText.setText(text1)

        }
        circularButton.setImageDrawable(IconDrawable(getActivity(), Iconify.IconValue.md_public).sizeDp(32).color(Color.WHITE))
        circularButton.setOnClickListener {
            getActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/yoavst/quickapps")))
        }
        recycler.setLayoutManager(LinearLayoutManager(getActivity()))
        val builder = LibsBuilder().withFields(javaClass<R.string>().getFields()).withAnimations(true)
        val adapter = LibsRecyclerViewAdapter(builder)
        adapter.setLibs(Libs(getActivity()).prepareLibraries(builder.internalLibraries, builder.excludeLibraries, builder.autoDetect, builder.sort))
        recycler.setAdapter(adapter)
    }
}