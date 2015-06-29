package com.yoavst.quickapps.toggles

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yoavst.quickapps.R
import com.yoavst.quickapps.toggles.TogglesListAdapter.VH

/**
 * Created by Yoav.
 */
public class TogglesListAdapter(val items: List<ToggleItem>) : RecyclerView.Adapter<VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.desktop_toggle_item, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items.get(position)
        holder.title.setText(item.name)
    }

    override fun getItemCount(): Int = items.size()

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public val title: TextView

        init {
            title = itemView.findViewById(android.R.id.text1) as TextView
        }
    }
}