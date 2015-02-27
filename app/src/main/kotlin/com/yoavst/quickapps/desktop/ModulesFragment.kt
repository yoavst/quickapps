package com.yoavst.quickapps.desktop

import android.app.Fragment
import android.support.v4.view.ViewPager
import com.viewpagerindicator.CirclePageIndicator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.view.Menu
import android.view.MenuInflater
import com.yoavst.quickapps.R
import android.graphics.Color
import com.malinskiy.materialicons.Iconify
import com.malinskiy.materialicons.IconDrawable
import android.app.AlertDialog
import com.yoavst.util.r
import android.support.v7.app.ActionBarActivity
import android.graphics.drawable.ColorDrawable
import kotlin.properties.Delegates
import butterknife.bindView
import com.mobsandgeeks.ake.viewById

/**
 * Created by Yoav.
 */
public class ModulesFragment : Fragment(), ViewPager.OnPageChangeListener {
    val statusBar: View by Delegates.lazy { viewById<View>(R.id.status_bar) }
    val pager: ViewPager by bindView(R.id.pager)
    val indicator: CirclePageIndicator by bindView(R.id.indicator)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.desktop_frgament_modules, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super<Fragment>.onViewCreated(view, savedInstanceState)
        val ta = getActivity().getResources().obtainTypedArray(R.array.icons_colors)
        colors = IntArray(ta.length())
        for (i in 0..ta.length() - 1) {
            colors[i] = ta.getColor(i, 0)
        }
        ta.recycle()
        pager.setAdapter(ModulesAdapter(getChildFragmentManager(), getActivity()))
        indicator.setViewPager(pager)
        indicator.setOnPageChangeListener(this)
        pager.post(r { onPageSelected(0) })
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        if (getActivity() != null) {
            (getActivity() as ActionBarActivity).getSupportActionBar().setBackgroundDrawable(ColorDrawable(colors[position]))
            (getActivity() as ActionBarActivity).getSupportActionBar().setTitle(pager.getAdapter().getPageTitle(position))
            statusBar.setBackgroundColor(colors[position])
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Fragment>.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super<Fragment>.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.modules, menu)
        val item = menu.findItem(R.id.jump)
        item.setOnMenuItemClickListener { item -> onJumpPressed() }
        item.setIcon(IconDrawable(getActivity(), Iconify.IconValue.md_open_with).color(Color.WHITE).sizeDp(24))
    }

    fun onJumpPressed(): Boolean {
        val builder = AlertDialog.Builder(getActivity())
        builder.setTitle(R.string.jump_to_setting)
        builder.setItems(R.array.modules) {(dialog, which) -> pager.setCurrentItem(which, true) }
        builder.setNegativeButton(android.R.string.no) {(dialog, which) -> dialog.dismiss() }
        builder.show()
        return true
    }

    class object {
        var colors: IntArray by Delegates.notNull()
    }
}