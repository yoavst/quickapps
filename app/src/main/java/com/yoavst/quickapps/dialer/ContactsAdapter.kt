package com.yoavst.quickapps.dialer

import android.support.v7.widget.RecyclerView
import android.telephony.PhoneNumberUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yoavst.kotlin.telephonyManager
import com.yoavst.quickapps.dialer.ContactsAdapter.VH


public class ContactsAdapter(activity: CDialerActivity, val callback: (number: String) -> Unit) : RecyclerView.Adapter<VH>() {
    val items = activity.phones
    val iso = activity.telephonyManager().getSimCountryIso().toUpperCase().trim()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH? {
        val layout = LayoutInflater.from(parent!!.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false)
        val outValue = TypedValue()
        parent.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        layout.setBackgroundResource(outValue.resourceId)
        return VH(layout)

    }

    override fun onBindViewHolder(holder: VH?, position: Int) {
        val item = items.get(position)
        val number = PhoneNumberUtils.formatNumber(item.parsed?.getRawInput() ?: item.name, iso)
        holder!!.itemView.setTag(number)
        holder.title.setText(item.name)
        holder.number.setText(number)
        holder.itemView.setOnClickListener { callback((it.getTag() as? String).orEmpty()) }
    }

    override fun getItemCount(): Int = items.size()

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public val title: TextView
        public val number: TextView

        init {
            title = itemView.findViewById(android.R.id.text1) as TextView
            number = itemView.findViewById(android.R.id.text2) as TextView
        }
    }
}