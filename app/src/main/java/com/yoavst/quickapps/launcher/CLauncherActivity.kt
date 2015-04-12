package com.yoavst.quickapps.launcher

import com.yoavst.quickapps.util.QCircleActivity
import java.lang.reflect.Type
import com.yoavst.util.typeToken
import java.util.ArrayList
import com.google.gson.Gson
import kotlin.properties.Delegates
import com.google.gson.GsonBuilder
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.ComponentName
import com.yoavst.quickapps.PrefManager
import com.lge.qcircle.template.QCircleTemplate
import com.lge.qcircle.template.TemplateType
import com.yoavst.quickapps.R
import android.net.Uri
import android.content.ContentValues
import android.content.ContentUris
import android.os.Bundle
import android.graphics.Color
import android.view.View
import com.lge.qcircle.template.TemplateTag
import android.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.ImageView
import com.makeramen.RoundedImageView
import android.content.IntentFilter
import com.yoavst.kotlin.toPx

/**
 * Created by Yoav.
 */
public class CLauncherActivity : QCircleActivity(), View.OnClickListener {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    override fun getIntentToShow(): Intent? = null
    private val appInstalledReceiver = AppInstalledReceiver(this)
    private val appRemovedReceiver = AppRemovedReceiver(this)
    var items: MutableList<ListItem> by Delegates.notNull()
    var views: MutableList<View> by Delegates.notNull()
    val iconSize: Int by Delegates.lazy { 64.toPx(this) }
    val marginSize: Int by Delegates.lazy { 10.toPx(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<QCircleActivity>.onCreate(savedInstanceState)
        template.setBackButton()
        template.setBackgroundColor(Color.BLACK, false)
        template.setBackButtonTheme(true)
        setContentView(template.getView())
        val showItemsOnLG = preferences.showAppsThatInLg().getOr(false)
        if (components == null && !showItemsOnLG)
            updateComponents(this)
        val allItems = getIconsFromPrefs(this)
        if (!showItemsOnLG) {
            items = ArrayList<ListItem>(allItems.size())
            for (item in allItems)
                if (item.enabled && item.canBeShown()) {
                    items.add(item)
                }
        } else items = allItems
        views = ArrayList<View>(items.size())
        var isVertical = preferences.launcherIsVertical().getOr(true)
        if (items.size() < 5) {
            isVertical = true
        }
        getFragmentManager()
                .beginTransaction()
                .replace(TemplateTag.CONTENT_MAIN, if (isVertical) VerticalFragment() else HorizontalFragment())
                .commit()
    }

    override fun onClick(v: View) {
        val intent = Intent("com.lge.quickcover")
        val componentName = v.getTag().toString()
        if (componentName == "com.lge.camera/com.lge.camera.app.QuickWindowCameraActivity") {
            intent.setAction("com.lge.android.intent.action.STILL_IMAGE_CAMERA_COVER")
        }
        intent.setComponent(ComponentName.unflattenFromString(componentName))
        startActivity(intent)
        for (view in views) {
            view.setEnabled(false)
        }
        finish()
    }

    protected override fun onResume() {
        super<QCircleActivity>.onResume()
        val filterAdd = IntentFilter(Intent.ACTION_PACKAGE_ADDED)
        filterAdd.addDataScheme("package")
        registerReceiver(appInstalledReceiver, filterAdd)
        val filterRemove = IntentFilter(Intent.ACTION_PACKAGE_REMOVED)
        filterRemove.addDataScheme("package")
        registerReceiver(appRemovedReceiver, filterRemove)
    }

    protected override fun onPause() {
        unregisterReceiver(appInstalledReceiver)
        unregisterReceiver(appRemovedReceiver)
        super<QCircleActivity>.onPause()
    }

    public inner class VerticalFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.launcher_circle_vertical, container, false)
            val layout = view.findViewById(R.id.table_layout) as TableLayout
            val tableParams = TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val rowParams = TableRow.LayoutParams(iconSize, iconSize)
            tableParams.setMargins(0, marginSize, 0, 0)
            rowParams.setMargins(0, 0, marginSize, 0)
            var lastRow: TableRow? = null
            for (i in items.indices) {
                if (i % 2 == 0) {
                    lastRow = TableRow(getActivity())
                    lastRow!!.setLayoutParams(tableParams)
                    layout.addView(lastRow)
                }
                if (lastRow != null) {
                    val icon = setOnClick(createLauncherIcon(items.get(i), rowParams))
                    views.add(icon)
                    lastRow!!.addView(icon)
                }
            }
            return view
        }

    }

    public inner class HorizontalFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.launcher_circle_horizontal, container, false)
            val layout = view.findViewById(R.id.table_layout) as TableLayout
            val maxItemsPerLine = if (items.size() % 2 == 0) items.size() / 2 else (items.size() / 2 + 1)
            val tableParams = TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val rowParams = TableRow.LayoutParams(iconSize, iconSize)
            tableParams.setMargins(0, marginSize, 0, 0)
            rowParams.setMargins(0, 0, marginSize, 0)
            var lastRow: TableRow? = null
            for (i in items.indices) {
                if (i % maxItemsPerLine == 0) {
                    lastRow = TableRow(getActivity())
                    lastRow!!.setLayoutParams(tableParams)
                    layout.addView(lastRow)
                }
                val index = if (i < maxItemsPerLine) i * 2 else (i - maxItemsPerLine) * 2 + 1
                if (lastRow != null) {
                    val icon = setOnClick(createLauncherIcon(items.get(index), rowParams))
                    views.add(icon)
                    lastRow!!.addView(icon)
                }

            }
            return view
        }
    }

    private fun setOnClick(view: View): View {
        view.setOnClickListener(this)
        return view
    }

    private fun createLauncherIcon(item: ListItem, params: ViewGroup.LayoutParams): ImageView {
        val imageView = RoundedImageView(this)
        imageView.setLayoutParams(params)
        imageView.setBackground(null)
        imageView.setImageDrawable(item.icon)
        imageView.setTag(item.activity)
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER)
        imageView.setAdjustViewBounds(true)
        imageView.setCornerRadius(iconSize.toFloat() / 2.toFloat())
        imageView.setOval(false)
        imageView.setBorderColor(Color.BLACK)
        imageView.setBorderWidth(20.toFloat())
        return imageView
    }

    private inner class AppRemovedReceiver(private val activity: CLauncherActivity) : BroadcastReceiver() {

        public override fun onReceive(context: Context, intent: Intent) {
            val b = intent.getExtras()
            val uid = b.getInt(Intent.EXTRA_UID)
            val packages = context.getPackageManager().getPackagesForUid(uid)
            for (pkg in packages) {
                defaultItems?.forEach { item ->
                    val itemPkg = ComponentName.unflattenFromString(item.activity).getPackageName()
                    if (pkg == itemPkg) {
                        val i = activity.getIntent()
                        activity.finish()
                        defaultItems = null
                        startActivity(i)
                        return@onReceive
                    }
                }
            }
        }
    }

    private inner class AppInstalledReceiver(private val activity: CLauncherActivity) : BroadcastReceiver() {

        public override fun onReceive(context: Context, intent: Intent) {
            val b = intent.getExtras()
            val uid = b.getInt(Intent.EXTRA_UID)
            val packages = context.getPackageManager().getPackagesForUid(uid)
            val resInfoList = context.getPackageManager().queryIntentActivities(Intent("com.lge.quickcover"), 0)
            val quickcircleList = ArrayList<String>(resInfoList.size())
            for (info in resInfoList) {
                quickcircleList.add(ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name).flattenToString())
            }
            for (pkg in packages) {
                val info: PackageInfo
                try {
                    info = context.getPackageManager().getPackageInfo(pkg, PackageManager.GET_ACTIVITIES)
                } catch (e: PackageManager.NameNotFoundException) {
                    continue
                }
                for (aInfo in info.activities) {
                    val name = ComponentName(aInfo.applicationInfo.packageName, aInfo.name)
                    if (quickcircleList.contains(name.flattenToString())) {
                        val i = activity.getIntent()
                        activity.finish()
                        defaultItems = null
                        startActivity(i)
                        return
                    }
                }
            }
        }
    }

    fun ListItem.canBeShown(): Boolean {
        components?.forEach { name -> if (name.component.flattenToString() == this.activity) return false }
        return true
    }

    class object {
        public var listType: Type = typeToken<ArrayList<ListItem>>()
        public val gson: Gson by Delegates.lazy {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(javaClass<ListItem>(), LauncherDeSerializer())
            gsonBuilder.create()
        }
        public var defaultItems: ArrayList<ListItem>? = null
        private var components: Array<ComponentWrapper>? = null

        public fun getIconsFromPrefs(context: Context): ArrayList<ListItem> {
            val prefs = PrefManager(context)
            if (prefs.launcherItems().getOr("-1") == "-1") {
                initDefaultIcons(context)
                prefs.launcherItems().put(gson.toJson(defaultItems, listType)).apply()
                return defaultItems!!
            } else {
                val items: ArrayList<ListItem> = gson.fromJson(prefs.launcherItems().getOr("[]"), listType)
                if (defaultItems == null) initDefaultIcons(context)
                val autoAdd = prefs.launcherAutoAddModules().getOr(false)
                var i = items.size() - 1
                while (i >= 0) {
                    if (items.get(i) !in defaultItems!!) {
                        items.remove(i)
                    } else {
                        try {
                            items.get(i).icon = context.getPackageManager().getActivityIcon(ComponentName.unflattenFromString(items.get(i).activity))
                        } catch (e: PackageManager.NameNotFoundException) {
                            items.remove(i)
                        }

                    }
                    i--
                }
                defaultItems!!.forEach { item ->
                    if (!items.contains(item)) {
                        if (!autoAdd) {
                            item.enabled = false
                        }
                        items.add(item)
                    }
                }
                prefs.launcherItems().put(gson.toJson(items, listType)).apply()
                return items
            }
        }

        public fun initDefaultIcons(context: Context): ArrayList<ListItem> {
            val prefs = PrefManager(context)
            val loadExternal = prefs.launcherLoadExternalModules().getOr(false)
            if (defaultItems == null) {
                val myIntent = Intent("com.lge.quickcover")
                val resInfoList = context.getPackageManager().queryIntentActivities(myIntent, 0)
                defaultItems = ArrayList(resInfoList.size())
                val blacklist = context.getResources().getStringArray(R.array.launcher_blacklist)
                for (info in resInfoList) {
                    val name = info.loadLabel(context.getPackageManager()).toString()
                    val icon = info.loadIcon(context.getPackageManager())
                    val packageName = info.activityInfo.applicationInfo.packageName
                    if ((!loadExternal && packageName != context.getPackageName()) || info.activityInfo.name in blacklist)
                        continue
                    val activity = ComponentName(packageName, info.activityInfo.name).flattenToString()
                    val item = ListItem(name, icon, activity, true)
                    defaultItems!!.add(item)
                }
            }
            return defaultItems!!
        }

        public fun updateComponents(context: Context) {
            val uri = Uri.parse("content://com.lge.lockscreensettings/quickwindow")
            val cursor = context.getContentResolver().query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val localComponents = ArrayList<ComponentWrapper>()
                if (cursor.getCount() != 0) {
                    do {
                        localComponents.add(ComponentWrapper(ComponentName(cursor.getString(cursor.getColumnIndex("package")), cursor.getString(cursor.getColumnIndex("class"))),
                                cursor.getInt(cursor.getColumnIndex("_id"))))
                    } while ((cursor.moveToNext()))
                }
                cursor.close()
                components = localComponents.toArray(arrayOfNulls(localComponents.size()))
            } else {
                components = array()
            }
        }

        public fun hasSettings(context: Context): Boolean {
            updateComponents(context)
            var settings = -1
            components?.forEach { wrapper ->
                if (wrapper.component.getPackageName().equals("com.lge.clock")) {
                    settings = wrapper.id
                }
            }
            return settings != -1
        }

        public fun removeSettings(context: Context): Boolean {
            try {
                updateComponents(context)
                val uri = Uri.parse("content://com.lge.lockscreensettings/quickwindow")
                var settings = -1
                components?.forEach { wrapper ->
                    if (wrapper.component.getPackageName().equals("com.lge.clock")) {
                        settings = wrapper.id
                    }
                }
                if (settings != -1) {
                    val rows = context.getContentResolver().delete(ContentUris.withAppendedId(uri, settings.toLong()), null, null)
                    return rows > 0
                } else
                    return false
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        public fun addSettings(context: Context): Boolean {
            try {
                updateComponents(context)
                val uri = Uri.parse("content://com.lge.lockscreensettings/quickwindow")
                if (components != null && components!!.size() < 6) {
                    var settings = -1
                    val values = booleanArray(false, false, false, false, false, false)
                    components!!.forEach { wrapper ->
                        if (wrapper.component.getPackageName().equals("com.lge.clock")) {
                            settings = wrapper.id
                        }
                        values[wrapper.id - 1] = true
                    }
                    if (settings == -1) {
                        var missingId = -1
                        for (i in values.indices) {
                            if (!values[i]) {
                                missingId = i + 1
                                break
                            }
                        }
                        val newValues = ContentValues()
                        newValues.put("_id", missingId)
                        newValues.put("package", "com.lge.clock")
                        newValues.put("class", "com.lge.clock.quickcover.QuickCoverSettingActivity")
                        context.getContentResolver().insert(uri, newValues)
                        return true
                    } else
                        return false
                } else
                    return false
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
    }
}