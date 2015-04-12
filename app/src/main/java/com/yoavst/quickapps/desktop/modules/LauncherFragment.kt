package com.yoavst.quickapps.desktop.modules

import com.yoavst.quickapps.desktop.BaseModuleFragment
import com.yoavst.quickapps.R
import java.util.ArrayList
import kotlin.properties.Delegates
import android.widget.CompoundButton
import com.yoavst.kotlin.toast
import android.content.Context
import android.content.BroadcastReceiver
import android.content.Intent
import com.yoavst.quickapps.launcher.ListItem
import com.yoavst.quickapps.launcher.CLauncherActivity
import java.util.Collections
import android.content.IntentFilter
import android.support.v7.widget.RecyclerView
import com.yoavst.quickapps.MyLinearLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.yoavst.quickapps.DragSortRecycler
import android.app.AlertDialog
import com.google.gson.Gson
import com.yoavst.kotlin.lollipopOrNewer
import com.yoavst.quickapps.launcher.ItemsListAdapter


public class LauncherFragment : BaseModuleFragment() {
    override val CompoundButtonIds: IntArray = intArray(R.id.load_externalg_checkbox, R.id.auto_load_checkbox, R.id.remove_lg_checkbox, R.id.orientation_switch)
    override val rowsIds: IntArray = intArray(R.id.modules_load_external_row, R.id.modules_auto_load_row, R.id.modules_remove_lg_row, R.id.modules_orientation_row)
    override val layoutId: Int = R.layout.desktop_module_launcher
    var items: ArrayList<ListItem> by Delegates.notNull()

    override fun shouldCheck(id: Int): Boolean {
        return when (id) {
            R.id.load_externalg_checkbox -> prefs.launcherLoadExternalModules().getOr(false)
            R.id.auto_load_checkbox -> prefs.launcherAutoAddModules().getOr(false)
            R.id.remove_lg_checkbox -> prefs.showAppsThatInLg().getOr(false)
            R.id.orientation_switch -> prefs.launcherIsVertical().getOr(true)
            else -> false
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.getId()) {
            R.id.load_externalg_checkbox -> {
                prefs.launcherLoadExternalModules().put(isChecked).apply()
            }
            R.id.auto_load_checkbox -> prefs.launcherAutoAddModules().put(isChecked).apply()
            R.id.remove_lg_checkbox -> prefs.showAppsThatInLg().put(isChecked).apply()
            R.id.orientation_switch -> prefs.launcherIsVertical().put(isChecked).apply()
        }
        toast(R.string.changed_successfully)
    }

    override fun init() {
        super.init()
        initItems()
        getActivity().findViewById(R.id.modules_order_row).setOnClickListener { v ->
            val adapter = ItemsListAdapter(items)
            val recyclerView = RecyclerView(getActivity())
            recyclerView.setHasFixedSize(true)
            recyclerView.setLayoutManager(LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false))
            recyclerView.setAdapter(adapter)
            val dragSortRecycler = DragSortRecycler()
            dragSortRecycler.setViewHandleId(R.id.drag_handle)
            dragSortRecycler.setOnItemMovedListener { from, to ->
                val item = items.remove(from);
                items.add(to, item);
                adapter.notifyDataSetChanged();
            }
            recyclerView.addItemDecoration(dragSortRecycler)
            recyclerView.addOnItemTouchListener(dragSortRecycler)
            recyclerView.setOnScrollListener(dragSortRecycler.getScrollListener())
            (if (lollipopOrNewer())
                AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
            else AlertDialog.Builder(getActivity()))
                    .setPositiveButton(android.R.string.yes) { dialog, which ->
                        prefs.launcherItems().put(CLauncherActivity.gson.toJson(items, CLauncherActivity.listType)).apply()
                        sortItems()
                    }
                    .setNegativeButton(android.R.string.no) { dialog, which ->
                        initItems()
                    }
                    .setView(recyclerView).show()
        }
    }

    fun initItems() {
        if (prefs.launcherItems().getOr("-1") != "-1")
            items = CLauncherActivity.getIconsFromPrefs(getActivity())
        else
            items = CLauncherActivity.initDefaultIcons(getActivity())
        sortItems()
    }


    public override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addDataScheme("package")
        getActivity().registerReceiver(installReceiver, filter)
    }

    public override fun onPause() {
        super.onPause()
        getActivity().unregisterReceiver(installReceiver)
    }

    var installReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        public override fun onReceive(context: Context, intent: Intent) {
            CLauncherActivity.defaultItems = null
            items = CLauncherActivity.getIconsFromPrefs(getActivity())
            CLauncherActivity.initDefaultIcons(getActivity())
            sortItems()
        }
    }

    fun sortItems() {
        if (items.size() != 0) {
            // We got to keep the same sort for checked, and put the unchecked at the end
            val checked: ArrayList<ListItem> = ArrayList()
            val unchecked: ArrayList<ListItem> = ArrayList()
            for (item in items) {
                if (!item.enabled)
                    unchecked.add(item)
                else
                    checked.add(item)
            }
            Collections.sort(unchecked)
            checked.addAll(unchecked)
            items = checked
        }
    }
}