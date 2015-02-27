package com.yoavst.quickapps.launcher

import android.graphics.drawable.Drawable

public class ListItem(public var name: String, public var icon: Drawable?, public var activity: String, public var enabled: Boolean): Comparable<ListItem> {
    override fun compareTo(other: ListItem): Int = name.compareTo(other.name)
    override fun hashCode(): Int {
        return activity.hashCode() + 29
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other === this) {
            return true
        }
        if (other.javaClass == this.javaClass) {
            val item = other as ListItem
            if (this.activity == item.activity) {
                return true
            }
        }
        return false
    }
}