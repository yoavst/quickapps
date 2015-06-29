package com.yoavst.quickapps.desktop

import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.SharedElementCallback
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.yoavst.kotlin.colorRes
import com.yoavst.kotlin.hide
import com.yoavst.kotlin.intent
import com.yoavst.kotlin.show
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.getBackgroundDrawable
import com.yoavst.quickapps.tools.hideAds
import kotlinx.android.synthetic.desktop_activity_main.*

/**
 * Created by yoavst.
 */
public class MainActivity : AppCompatActivity() {
    var exiting = false
    val fragment: Int
        get() {
            return DataActivity.currentFragment
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.desktop_activity_main)
        toolbar.setTitle("")
        setSupportActionBar(toolbar)
        initViews()
        initAds()
        ActivityCompat.setExitSharedElementCallback(this, object : SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                if (!exiting) {
                    if (sharedElements != null) {
                        val toolbarTransition = getString(R.string.transition_toolbar)
                        val nameTransition = getString(R.string.transition_name)
                        if (toolbarTransition in sharedElements)
                            sharedElements.put(toolbarTransition, getRightFrameLayout(fragment))
                        if (nameTransition in sharedElements)
                            sharedElements.put(nameTransition, getRightText(fragment))
                    }
                } else exiting = false
            }
        })
    }


    fun getRightFrameLayout(id: Int): View {
        return when (id) {
            DataActivity.FragmentHowToAdd -> howToAdd
            DataActivity.FragmentSettings -> settings
            DataActivity.FragmentSource -> source
            DataActivity.FragmentAbout -> about
            else -> howToAdd
        }
    }

    fun getRightText(id: Int): View {
        return when (id) {
            DataActivity.FragmentHowToAdd -> howToAddText
            DataActivity.FragmentSettings -> settingsText
            DataActivity.FragmentSource -> sourceText
            DataActivity.FragmentAbout -> aboutText
            else -> howToAdd
        }
    }

    fun initViews() {
        val rippleColor = colorRes(R.color.ripple_material_dark)
        howToAdd.setBackground(getBackgroundDrawable(colorRes(R.color.howToAdd), rippleColor))
        settings.setBackground(getBackgroundDrawable(colorRes(R.color.settings), rippleColor))
        source.setBackground(getBackgroundDrawable(colorRes(R.color.source), rippleColor))
        about.setBackground(getBackgroundDrawable(colorRes(R.color.about), rippleColor))
        howToAdd.setOnClickListener {
            startDataFragment(howToAdd, howToAddText, DataActivity.FragmentHowToAdd)
        }
        settings.setOnClickListener {
            startDataFragment(settings, settingsText, DataActivity.FragmentSettings)

        }
        source.setOnClickListener {
            startDataFragment(source, sourceText, DataActivity.FragmentSource)
        }
        about.setOnClickListener {
            startDataFragment(about, aboutText, DataActivity.FragmentAbout)

        }
    }

    fun startDataFragment(view: View, textView: TextView, fragmentId: Int) {
        exiting = true
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                Pair<View, String>(view, getString(R.string.transition_toolbar)),
                Pair<View, String>(textView, getString(R.string.transition_name)))
        ActivityCompat.startActivity(this, intent<DataActivity>().putExtra(DataActivity.ExtraFragment, fragmentId), options.toBundle())
    }

    fun initAds() {
        adView.setAdListener(object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adView.show()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                super.onAdFailedToLoad(errorCode)
                adView.hide()
            }
        });
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addKeyword("Quick Circle, LG G3, LG G4")
                .build()
        adView.loadAd(adRequest)
        if (!hideAds) {
            val interstitial = InterstitialAd(getApplicationContext())
            interstitial.setAdUnitId("ca-app-pub-3328409722635254/7430313728")
            interstitial.loadAd(adRequest)
            interstitial.setAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    if (isVisible || DataActivity.isVisible)
                        interstitial.show()
                }

            })
        }
    }

    override fun onResume() {
        super.onResume()
        isVisible = true
    }

    override fun onPause() {
        super.onPause()
        isVisible = false
    }

    companion object {
        public var isVisible: Boolean = false
    }

}