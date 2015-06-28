package com.yoavst.quickapps.desktop

import android.app.Fragment
import android.content.ActivityNotFoundException
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
import com.yoavst.kotlin.colorResource
import com.yoavst.kotlin.toast
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.bold
import com.yoavst.quickapps.tools.colorize
import com.yoavst.quickapps.tools.setBigger
import kotlinx.android.synthetic.desktop_fragment_about.about
import kotlinx.android.synthetic.desktop_fragment_about.donate
import kotlinx.android.synthetic.desktop_fragment_about.message
import java.util.Locale

public class AboutFragment : Fragment() {
    val fragmentColor by colorResource(R.color.about)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.desktop_fragment_about, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Locale.getDefault().getLanguage().startsWith("en")) {
            val text1 = SpannableString(getString(R.string.about))
            text1.setBigger(1.5F, 0, 17).colorize(fragmentColor, 0, 17)
            text1.setBigger(2F, 42, 59).colorize(fragmentColor, 42, 59).bold(42, 59)
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