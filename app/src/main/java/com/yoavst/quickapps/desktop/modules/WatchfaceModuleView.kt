package com.yoavst.quickapps.desktop.modules

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.yoavst.kotlin.appWidgetManager
import com.yoavst.kotlin.beforeVersion
import com.yoavst.kotlin.hide
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.BaseModuleView
import com.yoavst.quickapps.tools.*
import com.yoavst.quickapps.watchfaces.DigitalWatchfaceProvider

/**
 * Created by yoavst.
 */
public class WatchfaceModuleView : BaseModuleView {
    public constructor(context: Context) : super(context) {
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun init() {
        super.init()
        if (beforeVersion(21)) {
            hide()
        } else {
            addSettingView(R.string.am_pm_title, R.string.am_pm_subtitle, getContext().digitalWatchfaceAmPm) {
                getContext().digitalWatchfaceAmPm = it!!
                update()
                toastSuccess()
            }
            addColorView(R.string.main_background_text, R.string.main_background_subtext, getContext().digitalWatchfaceMainBackgroundColor) {
                getContext().digitalWatchfaceMainBackgroundColor = it
            }
            addColorView(R.string.secondary_background_text, R.string.secondary_background_subtext, getContext().digitalWatchfaceSecondaryBackgroundColor) {
                getContext().digitalWatchfaceSecondaryBackgroundColor = it
            }
            addColorView(R.string.hour_color_text, null, getContext().digitalWatchfaceHoursColor) {
                getContext().digitalWatchfaceHoursColor = it
            }
            addColorView(R.string.minute_color_text, null, getContext().digitalWatchfaceMinutesColor) {
                getContext().digitalWatchfaceMinutesColor = it
            }
            addColorView(R.string.date_color_text, null, getContext().digitalWatchfaceDateColor) {
                getContext().digitalWatchfaceDateColor = it
            }
            addColorView(R.string.am_pm_color_text, null, getContext().digitalWatchfaceAmPmColor) {
                getContext().digitalWatchfaceAmPmColor = it
            }
        }
    }

    fun setColorDialog(layout: View, colorView: ImageView, color: Int, callback: (newColor: Int) -> Unit) {
        layout.setTag(color)
        layout.setOnClickListener {
            ColorPickerDialogBuilder
                    .with(getContext())
                    .setTitle(getContext().getString(R.string.choose_color))
                    .initialColor(layout.getTag() as Int)
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setPositiveButton(getContext().getString(android.R.string.ok)) { dialog, selected, allColors ->
                        callback(selected)
                        colorView.setBackgroundColor(selected)
                        layout.setTag(selected)
                        update()
                    }.setNegativeButton(getContext().getString(android.R.string.cancel)) { dialog, which ->

            }.build().show()
        }
        colorView.setOnClickListener { layout.performClick() }
        colorView.setBackgroundColor(color)
    }

    fun update() {
        val intent = Intent(getContext(), javaClass<DigitalWatchfaceProvider>())
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        val appWidgetManager = getContext().appWidgetManager()
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                appWidgetManager.getAppWidgetIds(ComponentName(getContext(), javaClass<DigitalWatchfaceProvider>())))
        getContext().sendBroadcast(intent)
    }

    fun addColorView(title: Int, subtitle: Int?, currentColor: Int, callback: (newColor: Int) -> Unit) {
        val added = LayoutInflater.from(getContext()).inflate(R.layout.desktop_view_line_color, layout, false) as ViewGroup
        layout.addView(added)
        setColorDialog(added, added.getChildAt(1) as ImageView, currentColor, callback)
        ((added getChildAt 0) as ViewGroup getChildAt 0) as TextView setText title
        if (subtitle != null)
            ((added getChildAt 0) as ViewGroup getChildAt 1) as TextView setText subtitle
    }

    override fun getName(): Int = R.string.watchface_name

    override fun getIcon(): Int? = null
}