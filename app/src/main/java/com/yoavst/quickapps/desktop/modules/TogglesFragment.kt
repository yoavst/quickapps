package com.yoavst.quickapps.desktop.modules

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CompoundButton
import com.google.gson.Gson
import com.yoavst.kotlin.lollipopOrNewer
import com.yoavst.kotlin.toast
import com.yoavst.kotlin.viewById
import com.yoavst.quickapps.DragSortRecycler
import com.yoavst.quickapps.MyLinearLayoutManager
import com.yoavst.quickapps.PrefManager
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.BaseModuleFragment
import com.yoavst.quickapps.toggles.ToggleItem
import com.yoavst.quickapps.toggles.TogglesListAdapter
import com.yoavst.util.typeToken
import java.lang.reflect.Type
import java.util.ArrayList
import kotlin.properties.Delegates


/**
 * Created by Yoav.
 */
public class TogglesFragment : BaseModuleFragment() {
    override val CompoundButtonIds: IntArray = intArray(R.id.battery_checkbox)
    override val rowsIds: IntArray = intArray(R.id.battery_row)
    override val layoutId: Int = R.layout.desktop_module_toggles
    var items: ArrayList<ToggleItem> by Delegates.notNull()

    override fun shouldCheck(id: Int): Boolean = prefs.showBatteryToggle().getOr(true)
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        prefs.showBatteryToggle().put(isChecked).apply()
        toast(R.string.changed_successfully)
    }

    override fun init() {
        super.init()
        initItems()
        viewById<View>(R.id.toggles_order_row).setOnClickListener {
            val adapter = TogglesListAdapter(items)
            val recyclerView = RecyclerView(getActivity())
            recyclerView.setHasFixedSize(true)
            recyclerView.setLayoutManager(MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false))
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
                    .setPositiveButton(android.R.string.yes) { dialog, which -> prefs.togglesItems().put(Gson().toJson(items, TOGGLES_TYPE)).apply() }
                    .setNegativeButton(android.R.string.no) { dialog, which -> initItems() }
                    .setView(recyclerView).show()
        }
    }

    private fun initItems() {
        if (prefs.togglesItems().getOr("-1").equals("-1"))
            items = initDefaultToggles(getActivity())
        else
            items = Gson().fromJson(prefs.togglesItems().getOr("[]"), TOGGLES_TYPE)
        if (Build.VERSION.SDK_INT >= 21) {
            for (i in 0..items.size() - 1) {
                if (items.get(i).id == 1) {
                    items.remove(i)
                    break
                }
            }
        }
    }

    companion object {
        public val TOGGLES_TYPE: Type = typeToken<ArrayList<ToggleItem>>()

        public fun initDefaultToggles(context: Context): ArrayList<ToggleItem> {
            val toggles: Array<String> = context.getResources().getStringArray(R.array.toggles);
            val items = ArrayList<ToggleItem>(toggles.size());
            toggles.forEachIndexed { index, item -> items.add(ToggleItem(item, index)) }
            PrefManager(context).togglesItems().put(Gson().toJson(items, TOGGLES_TYPE)).apply()
            return items
        }
    }
}