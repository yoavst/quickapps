package com.yoavst.quickapps.desktop.modules

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import com.google.gson.Gson
import com.yoavst.kotlin.lollipopOrNewer
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.BaseModuleView
import com.yoavst.quickapps.toggles.ToggleItem
import com.yoavst.quickapps.toggles.TogglesAdapter
import com.yoavst.quickapps.toggles.TogglesListAdapter
import com.yoavst.quickapps.tools.*
import java.lang.reflect.Type
import java.util.ArrayList
import kotlin.properties.Delegates


public class TogglesModuleView : BaseModuleView {
    var items: MutableList<ToggleItem>? = null

    public constructor(context: Context) : super(context) {
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun init() {
        super.init()
        if (!isInEditMode()) {
            initItems()
            addSettingView(R.string.toggles_order_items, R.string.toggles_order_items_subtitle) {
                val adapter = TogglesListAdapter(items!!)
                val recyclerView = RecyclerView(getContext())
                recyclerView.setHasFixedSize(true)
                recyclerView.setLayoutManager(MyLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false))
                recyclerView.setAdapter(adapter)
                val dragSortRecycler = DragSortRecycler()
                dragSortRecycler.setViewHandleId(R.id.drag_handle)
                dragSortRecycler.setOnItemMovedListener { from, to ->
                    val item = items!!.remove(from)
                    items!!.add(to, item)
                    adapter.notifyDataSetChanged()
                }
                recyclerView.addItemDecoration(dragSortRecycler)
                recyclerView.addOnItemTouchListener(dragSortRecycler)
                recyclerView.addOnScrollListener(dragSortRecycler.getScrollListener())
                (if (lollipopOrNewer())
                    AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                else AlertDialog.Builder(getContext()))
                        .setPositiveButton(android.R.string.ok) { dialog, which ->
                           getContext().togglesItems = Gson().toJson(items, TogglesType)
                        }.setNegativeButton(android.R.string.no) { dialog, which -> initItems() }
                         .setView(recyclerView).show()
            }
        }
        addSettingView(R.string.battery_title, R.string.battery_subtitle, getContext().showBatteryToggle) {
            getContext().showBatteryToggle = it!!
        }
    }

    fun initItems() {
        if (getContext().togglesItems == "-1")
            items = TogglesAdapter.initDefaultToggles(getContext())
        else
            items = Gson().fromJson<MutableList<ToggleItem>>(getContext().togglesItems, TogglesType)
    }

    override fun getName(): Int = R.string.toggles_module_name

    override fun getIcon(): Int = R.drawable.qcircle_icon_toggles

    companion object {
        public val TogglesType: Type = typeToken<ArrayList<ToggleItem>>()
    }
}