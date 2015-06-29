package com.yoavst.quickapps.desktop.modules

import android.app.AlertDialog
import android.content.ComponentName
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import com.yoavst.kotlin.hide
import com.yoavst.kotlin.intent
import com.yoavst.kotlin.toast
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.BaseModuleView
import com.yoavst.quickapps.desktop.LaunchAdminActivity
import com.yoavst.quickapps.tools.g2Mode
import com.yoavst.quickapps.tools.hideAds
import java.util.ArrayList


public class GeneralSettingsView : BaseModuleView {
    public constructor(context: Context) : super(context) {
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun init() {
        super.init()
        if (!isInEditMode()) {
            addSettingView { layout, checkbox, title, subtitle ->
                checkbox.setChecked(getContext().hideAds)
                layout.setOnClickListener { checkbox.toggle() }
                checkbox.setOnCheckedChangeListener { compoundButton, b ->
                    if (b) {
                        AlertDialog.Builder(getContext())
                                .setTitle(R.string.hide_ads)
                                .setMessage(R.string.hide_ads_explain)
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.ok) { dialog, id ->
                                    dialog.dismiss()
                                    getContext().hideAds = true
                                }
                                .setNegativeButton(android.R.string.cancel) { dialog, id ->
                                    dialog.dismiss()
                                    compoundButton.setChecked(false)
                                }

                                .show()

                    } else getContext().hideAds = false
                }
                title.setText(R.string.hide_ads)
                subtitle.setText(R.string.hide_ads_explain_short)
            }
            if (Build.VERSION.SDK_INT < 22)
                addSettingView { layout, checkbox, title, subtitle ->
                    checkbox.hide()
                    title.setText(settingsText())
                    layout.setOnClickListener {
                        val isSuccess =
                                if (hasSettings(getContext()))
                                    removeSettings(getContext())
                                else addSettings(getContext())
                        if (isSuccess) {
                            getContext().toast(R.string.reboot_for_update)
                            title.setText(settingsText())
                        } else {
                            getContext().toast(R.string.error)
                        }
                    }
                }
            addSettingView(R.string.g2_mode, R.string.g2_mode_explain, getContext().g2Mode) {
                getContext().g2Mode = it!!
            }
            addSettingView(R.string.admin_permission, R.string.admin_permission_explain) {
                getContext().startActivity(getContext().intent<LaunchAdminActivity>())
            }
        }
    }

    fun settingsText(): Int {
        return if (hasSettings(getContext())) R.string.hide_settings_from_quick_circle else R.string.show_settings_from_quick_circle
    }


    override fun getName(): Int = R.string.settings

    override fun getIcon(): Int? = null

    companion object {
        private fun hasSettings(context: Context): Boolean {
            val components = getComponents(context)
            var settings = -1
            components.forEach { wrapper ->
                if (wrapper.first.getPackageName().equals("com.lge.clock")) {
                    settings = wrapper.second
                }
            }
            return settings != -1
        }

        private fun removeSettings(context: Context): Boolean {
            try {
                val components = getComponents(context)
                val uri = Uri.parse("content://com.lge.lockscreensettings/quickwindow")
                var settings = -1
                components.forEach { wrapper ->
                    if (wrapper.first.getPackageName().equals("com.lge.clock")) {
                        settings = wrapper.second
                    }
                }
                if (settings != -1) {
                    val rows = context.getContentResolver().delete(ContentUris.withAppendedId(uri, settings.toLong()), null, null)
                    return rows > 0
                } else
                    return false
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        private fun addSettings(context: Context): Boolean {
            try {
                val components = getComponents(context)
                val uri = Uri.parse("content://com.lge.lockscreensettings/quickwindow")
                if (components.size() < 6) {
                    var settings = -1
                    val values = booleanArrayOf(false, false, false, false, false, false)
                    components.forEach { wrapper ->
                        if (wrapper.first.getPackageName().equals("com.lge.clock")) {
                            settings = wrapper.second
                        }
                        values[wrapper.second - 1] = true
                    }
                    if (settings == -1) {
                        var missingId = -1
                        for (i in values.indices) {
                            if (!values[i]) {
                                missingId = i + 1
                                break
                            }
                        }
                        val newValues = ContentValues()
                        newValues.put("_id", missingId)
                        newValues.put("package", "com.lge.clock")
                        newValues.put("class", "com.lge.clock.quickcover.QuickCoverSettingActivity")
                        context.getContentResolver().insert(uri, newValues)
                        return true
                    } else
                        return false
                } else
                    return false
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        public fun getComponents(context: Context): List<Pair<ComponentName, Int>> {
            val uri = Uri.parse("content://com.lge.lockscreensettings/quickwindow")
            val cursor = context.getContentResolver().query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val localComponents = ArrayList<Pair<ComponentName, Int>>()
                if (cursor.getCount() != 0) {
                    do {
                        localComponents.add(Pair(ComponentName(cursor.getString(cursor.getColumnIndex("package")), cursor.getString(cursor.getColumnIndex("class"))),
                                cursor.getInt(cursor.getColumnIndex("_id"))))
                    } while ((cursor.moveToNext()))
                }
                cursor.close()
                return localComponents
            } else {
                return listOf()
            }
        }
    }
}