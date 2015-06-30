package com.yoavst.quickapps.launcher

import android.graphics.drawable.Drawable

public class ListItem(public var name: String, public var activity: String, public var enabled: Boolean, public var icon: Int = 0): Comparable<ListItem> {
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
        if (other is ListItem) {
            if (this.activity == other.activity) {
                return true
            }
        }
        return false
    }
}
