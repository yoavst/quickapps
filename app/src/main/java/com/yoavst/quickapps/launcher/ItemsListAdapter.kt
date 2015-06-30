package com.yoavst.quickapps.launcher

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.yoavst.kotlin.async
import com.yoavst.kotlin.mainThread
import com.yoavst.kotlin.toPx
import com.yoavst.quickapps.R
import com.yoavst.quickapps.launcher.ItemsListAdapter.VH
import java.lang.ref.WeakReference
import java.util.ArrayList

/**
 * Created by Yoav.
 */
public class ItemsListAdapter(val context: Context, val items: ArrayList<ListItem>) : RecyclerView.Adapter<VH>() {
    val imageSize = 20.toPx(context)
    val images: Array<WeakReference<BitmapDrawable>?> = arrayOfNulls(items.size())
    val running = BooleanArray(items.size())
    val packageManager = context.getPackageManager()
    val packageName = context.getPackageName()
    val resources = context.getResources()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.deskop_launcher_item, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item: ListItem = items[position]
        holder.name.setText(item.name)
        val bitmap = images[position]
        holder.icon.setImageDrawable(bitmap?.get())
        if (bitmap == null) {
            if (!running[position]) {
                running[position] = true
                async {
                    val resources = getResources(item.activity.splitToSequence('/').iterator().next())
                    val new = getBitmap(resources, item.icon)
                    images[position] = WeakReference(BitmapDrawable(resources, new))
                    mainThread {
                        holder.icon.setImageDrawable(images[position]!!.get())
                    }
                }
            }
        }
        holder.enabled.setChecked(item.enabled)
        holder.enabled.setTag(position)
        holder.enabled.setOnClickListener { v ->
            val check = v as CheckBox
            items[check.getTag() as Int].enabled = check.isChecked()
        }
    }

    private fun getBitmap(resources: Resources, id: Int): Bitmap {
        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, id, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.min(photoW / imageSize, photoH / imageSize)

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true

        val bitmap = BitmapFactory.decodeResource(resources, id, bmOptions)
        return bitmap
    }

    fun getResources(resourcesPackageName: String): Resources {
        return if (resourcesPackageName == packageName)
            resources
        else packageManager.getResourcesForApplication(resourcesPackageName)
    }

    public fun destroy() {
        images.forEach { (it?.get() as? BitmapDrawable)?.getBitmap()?.recycle() }
        System.gc()
    }

    override fun getItemCount(): Int = items.size()

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public val name: TextView
        public val enabled: CheckBox
        public val icon: ImageView

        init {
            name = itemView.findViewById(R.id.name) as TextView
            enabled = itemView.findViewById(R.id.enabled) as CheckBox
            icon = itemView.findViewById(R.id.icon) as ImageView
        }

    }
}