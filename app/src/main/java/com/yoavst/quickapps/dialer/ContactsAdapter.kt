package com.yoavst.quickapps.dialer

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.yoavst.quickapps.R
import android.view.ViewGroup
import android.view.LayoutInflater
import java.util.ArrayList
import android.content.Context
import com.yoavst.quickapps.dialer.ContactsAdapter.VH
import android.provider.ContactsContract
import kotlin.properties.Delegates
import android.graphics.Color
import android.util.TypedValue

/**
 * Created by Yoav.
 */
public class ContactsAdapter(context: Context, val callback: (number: String) -> Unit) : RecyclerView.Adapter<VH>() {
    val items: ArrayList<Pair<String, String>> by Delegates.lazy {
        val localItems: ArrayList<Pair<String, String>> = ArrayList()
        val phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, array(ContactsContract.CommonDataKinds.Phone.NUMBER, "display_name"), null, null, "display_name")
        phones.moveToFirst()
        while (phones.moveToNext()) {
            localItems.add(phones.getString(phones.getColumnIndex("display_name")).to(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))))
        }
        phones.close()
        localItems
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH? {
        val layout = LayoutInflater.from(parent!!.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false)
        val outValue = TypedValue()
        parent.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        layout.setBackgroundResource(outValue.resourceId)
        return VH(layout)

    }

    override fun onBindViewHolder(holder: VH?, position: Int) {
        val item = items.get(position)
        holder!!.itemView.setTag(item.second)
        holder.title.setText(item.first)
        holder.number.setText(item.second)
        holder.itemView.setOnClickListener { v -> callback(v.getTag() as String) }
    }

    override fun getItemCount(): Int = items.size()

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView: View) {
        public val title: TextView
        public val number: TextView

        init {
            title = itemView.findViewById(android.R.id.text1) as TextView
            number = itemView.findViewById(android.R.id.text2) as TextView
        }
    }
}