package com.yoavst.quickapps.launcher

import android.app.Fragment
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lge.qcircle.template.ButtonTheme
import com.lge.qcircle.template.QCircleBackButton
import com.lge.qcircle.template.TemplateTag
import com.yoavst.kotlin.e
import com.yoavst.kotlin.toPx
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import com.yoavst.quickapps.tools.launcherIsVertical
import com.yoavst.quickapps.tools.launcherItems
import com.yoavst.quickapps.tools.typeToken
import java.lang.reflect.Type
import java.util.ArrayList
import kotlin.properties.Delegates

/**
 * Created by yoavst.
 */
public class CLauncherActivity : QCircleActivity(), View.OnClickListener {
    override fun getIntentToShow(): Intent? = null
    var items: List<ListItem> by Delegates.notNull()
    var views: MutableList<View> by Delegates.notNull()
    val iconSize: Int by Delegates.lazy { 64.toPx(this) }
    val marginSize: Int by Delegates.lazy { 10.toPx(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<QCircleActivity>.onCreate(savedInstanceState)
        template.setBackgroundColor(Color.BLACK)
        template.addElement(QCircleBackButton(this, ButtonTheme.DARK))
        setContentView(template.getView())
        initItems()
        val isVertical = items.size() < 5 || launcherIsVertical
        views = ArrayList(items.size())
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


    public inner class VerticalFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.launcher_vertical_fragment, container, false)
            val layout = view.findViewById(R.id.table_layout) as TableLayout
            val tableParams = TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val rowParams = TableRow.LayoutParams(iconSize, iconSize)
            tableParams.setMargins(0, marginSize, 0, 0)
            rowParams.setMargins(0, 0, marginSize, 0)
            var lastRow: TableRow? = null
            for (i in items.indices) {
                if (i % 2 == 0) {
                    lastRow = TableRow(getActivity())
                    lastRow.setLayoutParams(tableParams)
                    layout.addView(lastRow)
                }
                if (lastRow != null) {
                    val item = items[i]
                    val icon = setOnClick(createLauncherIcon(item, rowParams))
                    views.add(icon)
                    lastRow.addView(icon)
                    setPic(getResources(item.activity.splitToSequence('/').iterator().next()), item.icon, icon)
                }
            }
            return view
        }

    }

    public inner class HorizontalFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.launcher_horizontal_fragment, container, false)
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
                    lastRow.setLayoutParams(tableParams)
                    layout.addView(lastRow)
                }
                val index = if (i < maxItemsPerLine) i * 2 else (i - maxItemsPerLine) * 2 + 1
                if (lastRow != null) {
                    val item = items[index]
                    val icon = setOnClick(createLauncherIcon(item, rowParams))
                    views.add(icon)
                    lastRow.addView(icon)
                    setPic(getResources(item.activity.splitToSequence('/').iterator().next()), item.icon, icon)
                }

            }
            return view
        }
    }

    private fun setOnClick(view: ImageView): ImageView {
        view.setOnClickListener(this)
        return view
    }

    private fun createLauncherIcon(item: ListItem, params: ViewGroup.LayoutParams): ImageView {
        val imageView = ImageView(this)
        imageView.setLayoutParams(params)
        imageView.setBackground(null)
        imageView.setTag(item.activity)
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER)
        imageView.setAdjustViewBounds(true)
        return imageView
    }

    fun initItems() {
        if (launcherItems == "{}") {
            items = initDefaultItems(this)
            launcherItems = gson.toJson(Pair.create(true, items))
        } else {
            var data = gson.fromJson<Pair<Boolean?, ArrayList<ListItem>>>(launcherItems, ListType)
            if (data == null || data.first == null || data.first == false) {
                items = initDefaultItems(this)
                launcherItems = gson.toJson(Pair.create(true, items))
            } else {
                items = data.second
                iconify(this, items)
            }
        }
    }

    override fun onDestroy() {
        super<QCircleActivity>.onDestroy()
        views.forEach {
            val drawable = ((it as ImageView).getDrawable() as? BitmapDrawable)
            drawable?.setCallback(null)
            drawable?.getBitmap()?.recycle()

        }
        System.gc()
    }

    var i = 0
    private fun setPic(resources: Resources, id: Int, destination: ImageView) {
        e("${++i}")
        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, id, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.min(photoW / iconSize, photoH / iconSize)

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true

        val bitmap = BitmapFactory.decodeResource(resources, id, bmOptions)
        e("$scaleFactor")
        destination.setImageBitmap(bitmap)
    }

    fun getResources(packageName: String): Resources {
        return if (packageName == getPackageName())
            getResources()
        else getPackageManager().getResourcesForApplication(packageName)
    }

    companion object {
        public val ListType: Type = typeToken<Pair<Boolean?, ArrayList<ListItem>>>()
        public val gson: Gson by Delegates.lazy {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(javaClass<ListItem>(), LauncherDeSerializer())
            gsonBuilder.create()
        }

        public fun initDefaultItems(context: Context): ArrayList<ListItem> {
            val myIntent = Intent("com.lge.quickcover")
            val myPackageName = context.getPackageName()
            val resInfoList = context.getPackageManager().queryIntentActivities(myIntent, 0)
            val defaultItems = ArrayList<ListItem>(resInfoList.size())
            val blacklist = context.getResources().getStringArray(R.array.launcher_blacklist)
            val packageManager = context.getPackageManager()
            for (info in resInfoList) {
                val name = info.loadLabel(packageManager).toString()
                val packageName = info.activityInfo.applicationInfo.packageName
                if ((packageName != myPackageName) || info.activityInfo.name.orEmpty().endsWith("Old") || info.activityInfo.name in blacklist)
                    continue
                val activity = ComponentName(packageName, info.activityInfo.name).flattenToString()
                val item = ListItem(name, activity, false, info.getIconResource())
                defaultItems.add(item)
            }
            return defaultItems
        }

        public fun iconify(context: Context, items: List<ListItem>) {
            val myIntent = Intent("com.lge.quickcover")
            val resInfoList = context.getPackageManager().queryIntentActivities(myIntent, 0)
            external@ for (info in resInfoList) {
                val activity = ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name).flattenToString()
                for (item in items) {
                    if (activity == item.activity) {
                        item.icon = info.getIconResource()
                        continue@external
                    }
                }
            }
        }
    }
}