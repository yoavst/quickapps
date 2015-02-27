package com.yoavst.quickapps.notifications

import android.app.Notification
import android.os.Build
import android.service.notification.StatusBarNotification

import java.util.ArrayList
import java.util.Collections
import com.yoavst.util.equalsContent
import kotlin.platform.platformStatic

/**
 * Created by Yoav.
 */
public object NotificationsManager {
    private val LOCK = Object()
    private var isWhatsapp = false
    public platformStatic var notifications: ArrayList<StatusBarNotification>? = null
        get
        set(newNotifications: ArrayList<StatusBarNotification>?) {
            synchronized (LOCK) {
                if (newNotifications == null || newNotifications.size() == 0)
                    $notifications = ArrayList(0)
                else if (isWhatsapp) {
                    val whatsapp = ArrayList<StatusBarNotification>()
                    var i = newNotifications.size() - 1
                    while (i >= 0) {
                        if (newNotifications.get(i).getPackageName() == "com.whatsapp") {
                            whatsapp.add($notifications!!.get(i))
                        } else {
                            val extras = newNotifications.get(i).getNotification().extras
                            val title = extras.getString(Notification.EXTRA_TITLE)
                            if (!(title == null || title.length() == 0))
                                $notifications!!.add(newNotifications.get(i))
                        }
                        i--
                    }
                    for (statusBarNotification in whatsapp) {
                        //FIXME
                        $notifications!!.add(statusBarNotification)
                    }
                } else {
                    $notifications = ArrayList(newNotifications.size())
                    var i = newNotifications.size() - 1
                    while (i >= 0) {
                        val extras = newNotifications.get(i).getNotification().extras
                        val title = extras.getCharSequence(Notification.EXTRA_TITLE)
                        if (!(title == null || title.length() == 0))
                            $notifications!!.add(newNotifications.get(i))
                        i--
                    }
                    sort()
                }
                isWhatsapp = false
            }
        }

    public fun getCount(): Int {
        return if (notifications == null) 0 else notifications!!.size()
    }

    public fun clean() {
        notifications = null
    }

    public fun addNotification(statusBarNotification: StatusBarNotification) {
        synchronized (LOCK) {
            if (notifications == null) {
                notifications = ArrayList<StatusBarNotification>()
            }
            notifications!!.add(statusBarNotification)
            sort()
        }
    }

    public fun removeNotification(statusBarNotification: StatusBarNotification) {
        synchronized (LOCK) {
            if (notifications != null) {
                var index = -1
                for (i in notifications!!.indices) {
                    if (notifications!!.get(i).equalsContent(statusBarNotification)) {
                        index = i
                        break
                    }
                }
                if (index != -1) notifications!!.remove(index)
            }
        }
    }

    private fun sort() {
        if (notifications!!.size() >= 2) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                Collections.sort(notifications) {(lhs, rhs) -> if (lhs.isClearable()) (if (rhs.isClearable()) 0 else -1) else (if (rhs.isClearable()) 0 else -1) }
            else
                Collections.sort(notifications) {(lhs, rhs) -> rhs.getNotification().priority - lhs.getNotification().priority }
        }
    }

    public fun whatsapp() {
        isWhatsapp = true
    }
}

