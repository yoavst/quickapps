package com.yoavst.quickapps.desktop

import android.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.yoavst.kotlin.colorRes
import com.yoavst.kotlin.hide
import com.yoavst.kotlin.show
import com.yoavst.quickapps.R
import kotlinx.android.synthetic.desktop_activity_data.*

/**
 * Created by yoavst.
 */
public class DataActivity : AppCompatActivity() {
    var pendingFragment: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setStatusBarColor(Color.TRANSPARENT)
        setContentView(R.layout.desktop_activity_data)
        toolbar.setTitle("")
        setSupportActionBar(toolbar)
        val listener: ActionBarDrawerToggle = object : ActionBarDrawerToggle(this, drawer, toolbar, 0, 0) {
            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                if (pendingFragment != null) loadFragment(pendingFragment!!)
            }
        }
        drawer.setDrawerListener(listener)
        toolbar.setNavigationIcon(R.drawable.ic_drawer)
        initAds()
        val fragment = getIntent().getIntExtra(ExtraFragment, 0)
        loadFragment(fragment)
        navigationView.getMenu().getItem(fragment).setChecked(true)
        navigationView.setNavigationItemSelectedListener {
            when (it.getItemId()) {
                R.id.howToAdd -> pendingFragment = FragmentHowToAdd
                R.id.settings -> pendingFragment = FragmentSettings
                R.id.source -> pendingFragment = FragmentSource
                R.id.about -> pendingFragment = FragmentAbout
            }
            it.setChecked(true)
            drawer.closeDrawer(navigationView)
            true
        }

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
    }

    fun loadFragment(id: Int) {
        pendingFragment = null
        var fragment: Fragment? = null
        when (id) {
            FragmentHowToAdd -> {
                colored.setBackgroundColor(colorRes(R.color.howToAdd))
                title.setText(R.string.how_to_add)
                drawer.setStatusBarBackgroundColor(colorRes(R.color.howToAddDark))
                fragment = HowToFragment()
            }
            FragmentSettings -> {
                colored.setBackgroundColor(colorRes(R.color.settings))
                title.setText(R.string.settings)
                drawer.setStatusBarBackgroundColor(colorRes(R.color.settingsDark))
                fragment = ModulesFragment()
            }
            FragmentSource -> {
                colored.setBackgroundColor(colorRes(R.color.source))
                title.setText(R.string.source)
                drawer.setStatusBarBackgroundColor(colorRes(R.color.sourceDark))
                fragment = SourceFragment()
            }
            FragmentAbout -> {
                colored.setBackgroundColor(colorRes(R.color.about))
                title.setText(R.string.about_title)
                drawer.setStatusBarBackgroundColor(colorRes(R.color.aboutDark))
                fragment = AboutFragment()

            }
        }
        currentFragment = id
        getFragmentManager().beginTransaction().replace(R.id.layout, fragment).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getFragmentManager().findFragmentById(R.id.layout).onActivityResult(requestCode,resultCode,data)
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
        public val ExtraFragment: String = "extra_fragment"
        public val FragmentHowToAdd: Int = 0
        public val FragmentSettings: Int = 1
        public val FragmentSource: Int = 2
        public val FragmentAbout: Int = 3

        public var currentFragment: Int = -1
        public var isVisible: Boolean = false

    }
}