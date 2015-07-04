package com.yoavst.quickapps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.util.Pair
import com.yoavst.kotlin.e
import com.yoavst.quickapps.launcher.CLauncherActivity
import com.yoavst.quickapps.launcher.ListItem
import com.yoavst.quickapps.tools.launcherItems
import java.util.ArrayList

public class UninstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val name = intent.getData()?.getSchemeSpecificPart()
        if (name != null && name.isNotEmpty()) {
            val apps = context.launcherItems
            if (apps != "{}") {
                var data = CLauncherActivity.gson.fromJson<Pair<Boolean?, ArrayList<ListItem>>>(apps, CLauncherActivity.ListType)
                if (data == null || data.first == null || !data.first!!) {

                } else {
                    for (i in (data.second.size() - 1) downTo 0) {
                        if (name in data.second[i].activity) {
                            data.second.remove(i)
                            context.launcherItems = CLauncherActivity.gson.toJson(data)
                            break
                        }
                    }
                }
            }
        }
    }

}