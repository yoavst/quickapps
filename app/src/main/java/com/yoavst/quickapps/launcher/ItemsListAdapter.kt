package com.yoavst.quickapps.launcher

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.yoavst.quickapps.R
import android.view.ViewGroup
import android.view.LayoutInflater
import java.util.ArrayList
import com.yoavst.quickapps.launcher.ItemsListAdapter.VH
import android.widget.CheckBox
import android.widget.ImageView

/**
 * Created by Yoav.
 */
public class ItemsListAdapter(val items: ArrayList<ListItem>) : RecyclerView.Adapter<VH>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {
        return VH(LayoutInflater.from(parent!!.getContext()).inflate(R.layout.desktop_module_launcher_item, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item: ListItem = items[position]
        holder.name.setText(item.name)
        holder.icon.setImageDrawable(item.icon)
        holder.enabled.setChecked(item.enabled)
        holder.enabled.setTag(position)
        holder.enabled.setOnClickListener { v ->
            val check = v as CheckBox
            items[check.getTag() as Int].enabled = check.isChecked()
        }
    }


    override fun getItemCount(): Int = items.size()

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView: View) {
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