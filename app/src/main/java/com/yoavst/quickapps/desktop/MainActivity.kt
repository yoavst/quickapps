package com.yoavst.quickapps.desktop

import android.app.Fragment
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.support.v7.app.ActionBarDrawerToggle
import android.view.View
import android.widget.AdapterView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.malinskiy.materialicons.IconDrawable
import com.malinskiy.materialicons.Iconify
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.yoavst.kotlin.colorResource
import com.yoavst.kotlin.hide
import com.yoavst.kotlin.show
import com.yoavst.kotlin.viewById
import com.yoavst.quickapps.PrefManager
import com.yoavst.quickapps.R
import kotlinx.android.synthetic.desktop_activity_main.statusBar
import kotlinx.android.synthetic.desktop_activity_main.toolbar

/**
 * Created by Yoav.
 */
public class MainActivity : ActionBarActivity() {
    val primaryColor: Int by colorResource(R.color.primary_color)
    var isVisible = false
    private var pendingFragment: PendingFragment? = null

    class PendingFragment(public val title: String, public val fragment: Fragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.desktop_activity_main)
        initDrawer()
        initAds()
    }

    override fun onPause() {
        super.onPause()
        isVisible = false
    }

    override fun onResume() {
        super.onResume()
        isVisible = true
    }

    fun initDrawer() {
        toolbar.setTitle("")
        setSupportActionBar(toolbar)
        val layout = Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        PrimaryDrawerItem().withName(R.string.how_to_add).withIcon(IconDrawable(this, Iconify.IconValue.md_help)),
                        PrimaryDrawerItem().withName(R.string.settings).withIcon(IconDrawable(this, Iconify.IconValue.md_settings)),
                        DividerDrawerItem(),
                        PrimaryDrawerItem().withName(R.string.source).withIcon(IconDrawable(this, Iconify.IconValue.md_adb)),
                        PrimaryDrawerItem().withName(R.string.about_title).withIcon(IconDrawable(this, Iconify.IconValue.md_account_circle)))
                .withHeader(R.layout.header_drawer)
                .withOnDrawerItemClickListener { adapterView: AdapterView<*>, view: View, position: Int, id: Long, iDrawerItem: IDrawerItem? ->
                    toolbar.setBackgroundColor(primaryColor)
                    statusBar.setBackgroundColor(primaryColor)
                    when (position) {
                        1 -> pendingFragment = PendingFragment(getString(R.string.how_to_add), HowToFragment())
                        2 -> pendingFragment = PendingFragment("", ModulesFragment())
                        4 -> pendingFragment = PendingFragment(getString(R.string.source), SourceFragment())
                        5 -> pendingFragment = PendingFragment(getString(R.string.about_title), AboutFragment())
                    }
                }
                .build().getDrawerLayout()
        val listener: ActionBarDrawerToggle = object : ActionBarDrawerToggle(this, layout, toolbar, 0, 0) {
            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                loadFragment()
            }
        }
        layout.setDrawerListener(listener)
        toolbar.setNavigationIcon(R.drawable.ic_drawer)
        pendingFragment = PendingFragment(getString(R.string.how_to_add), HowToFragment())
        loadFragment()
    }

    fun loadFragment() {
        pendingFragment?.let { pending ->
            getFragmentManager().beginTransaction().replace(R.id.content, pending.fragment).commit()
            toolbar.setTitle(pending.title)
            pendingFragment = null
        }
    }

    fun initAds() {
        val adView: AdView = viewById(R.id.adView)
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
                .addKeyword("Quick Circle, LG G3")
                .build()
        adView.loadAd(adRequest)
        if (!PrefManager(this).hideAds().getOr(false)) {
            val interstitial = InterstitialAd(this)
            interstitial.setAdUnitId("ca-app-pub-3328409722635254/7430313728")
            interstitial.loadAd(adRequest)
            interstitial.setAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    if (isVisible)
                        interstitial.show()
                }

            })
        }
    }
}