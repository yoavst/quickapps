package com.yoavst.quickapps.desktop

import android.app.Fragment
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.widget.TextView

import com.malinskiy.materialicons.IconDrawable
import com.malinskiy.materialicons.Iconify
import com.yoavst.quickapps.R

import java.util.Locale

import at.markushi.ui.CircleButton
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.yoavst.kotlin.colorResource
import kotlin.properties.Delegates
import com.yoavst.util.setBigger
import com.yoavst.util.colorize
import com.yoavst.util.bold
import com.yoavst.kotlin.toast
import kotlinx.android.synthetic.desktop_fragment_about.*
/**
 * Created by Yoav.
 */
public class AboutFragment : Fragment() {
    val blueColor by colorResource(R.color.primary_color_dark)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.desktop_fragment_about, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Locale.getDefault().getLanguage().startsWith("en")) {
            val text1 = SpannableString(getString(R.string.about))
            text1.setBigger(1.5F, 0, 17).colorize(blueColor, 0, 17)
            text1.setBigger(2F, 44, 49).colorize(blueColor, 44, 49).bold(44, 49)
            text1.setBigger(1.5F, 50, 67).colorize(blueColor, 50, 67)
            text1.setBigger(1.5F, 96, 102).colorize(blueColor, 96, 102)
            about.setText(text1)
        }
        donate.setImageDrawable(IconDrawable(getActivity(), Iconify.IconValue.md_payment).sizeDp(32).color(Color.WHITE))
        message.setImageDrawable(IconDrawable(getActivity(), Iconify.IconValue.md_messenger).sizeDp(32).color(Color.WHITE))
        donate.setOnClickListener {
            getActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://forum.xda-developers.com/donatetome.php?u=5053440")))
        }
        message.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "yoav.sternberg@gmail.com", null))
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            try {
                getActivity().startActivity(Intent.createChooser(emailIntent, getActivity().getString(R.string.about_mail_chooser)))
            } catch (exception: ActivityNotFoundException) {
                toast(R.string.about_intent_failed)
            }
        }
    }


}
