package com.yoavst.quickapps.desktop.modules

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.util.Pair
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import com.yoavst.kotlin.lollipopOrNewer
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.BaseModuleView
import com.yoavst.quickapps.launcher.CLauncherActivity
import com.yoavst.quickapps.launcher.ItemsListAdapter
import com.yoavst.quickapps.launcher.ListItem
import com.yoavst.quickapps.tools.DragSortRecycler
import com.yoavst.quickapps.tools.launcherIsVertical
import com.yoavst.quickapps.tools.launcherItems
import java.util.ArrayList

public class LauncherModuleView : BaseModuleView {
    var allItems = getItems()
    var oldAdapter: ItemsListAdapter? = null
    public constructor(context: Context) : super(context) {
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun init() {
        super.init()
        if (!isInEditMode()) {

            addSettingView(getResources().getString(R.string.orientation), "", getContext().launcherIsVertical) {
                getContext().launcherIsVertical = it!!
                toastSuccess()
            }
            addSettingView(R.string.launcher_control_items, R.string.launcher_control_items_subtitle) {
                val adapter = ItemsListAdapter(getContext(), allItems)
                val recyclerView = RecyclerView(getContext())
                recyclerView.setHasFixedSize(true)
                recyclerView.setLayoutManager(LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false))
                recyclerView.setAdapter(adapter)
                val dragSortRecycler = DragSortRecycler()
                dragSortRecycler.setViewHandleId(R.id.drag_handle)
                dragSortRecycler.setOnItemMovedListener { from, to ->
                    val item = allItems.remove(from)
                    allItems.add(to, item)
                    adapter.notifyDataSetChanged()
                }
                recyclerView.addItemDecoration(dragSortRecycler)
                recyclerView.addOnItemTouchListener(dragSortRecycler)
                recyclerView.addOnScrollListener(dragSortRecycler.getScrollListener())
                oldAdapter = adapter
                (if (lollipopOrNewer())
                    AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                else AlertDialog.Builder(getContext()))
                        .setPositiveButton(android.R.string.yes) { dialog, which ->
                            oldAdapter!!.destroy()
                            oldAdapter = null
                            val toSave = Pair.create(true, allItems filter { it.enabled })
                            getContext().launcherItems = CLauncherActivity.gson.toJson(toSave)
                            sort()
                        }
                        .setNegativeButton(android.R.string.no) { dialog, which ->
                            oldAdapter!!.destroy()
                            oldAdapter = null
                            sort()
                        }
                        .setView(recyclerView).show()
            }
        }
    }

    fun sort() {
        allItems = ArrayList(allItems.sortBy(comparator { l, r ->
            if (l.enabled) {
                if (r.enabled) l.name compareTo r.name
                else -1
            } else if (r.enabled) 1
            else l.name compareTo r.name
        }))
    }

    fun getItems(): ArrayList<ListItem> {
        var available: List<ListItem> = getAllAvailable(getContext())
        val text = getContext().launcherItems
        var items = arrayListOf<ListItem>()
        if (text != "{}") {
            val stored: Pair<Boolean?, ArrayList<ListItem>>? = CLauncherActivity.gson.fromJson(text, CLauncherActivity.ListType)
            if (stored != null && stored.first ?: false) {
                items = stored.second
            }
        }
        available.forEach {
            val item = items firstOrNull { item -> item == it }
            if (item != null) it.enabled = item.enabled
        }
        available = available.sortBy(comparator { l, r ->
            if (l.enabled) {
                if (r.enabled) l.name compareTo r.name
                else -1
            } else if (r.enabled) 1
            else l.name compareTo r.name
        })
//        available.forEach {
//            it.icon = getContext().getPackageManager().getActivityIcon(ComponentName.unflattenFromString(it.activity))
//        }
        return ArrayList(available)
    }

    override fun getName(): Int = R.string.launcher_module_name

    override fun getIcon(): Int = R.drawable.qcircle_icon_launcher

    companion object {
        public fun getAllAvailable(context: Context): ArrayList<ListItem> {
            val myIntent = Intent("com.lge.quickcover")
            val resInfoList = context.getPackageManager().queryIntentActivities(myIntent, 0)
            val defaultItems = ArrayList<ListItem>(resInfoList.size())
            val blacklist = context.getResources().getStringArray(R.array.launcher_blacklist)
            for (info in resInfoList) {
                val name = info.loadLabel(context.getPackageManager()).toString()
                val packageName = info.activityInfo.applicationInfo.packageName
                if (info.activityInfo.name in blacklist)
                    continue
                val activity = ComponentName(packageName, info.activityInfo.name).flattenToString()
                val item = ListItem(name, activity, false, info.getIconResource())
                defaultItems.add(item)
            }
            return defaultItems
        }
    }
}