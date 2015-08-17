package com.yoavst.quickapps.desktop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.yoavst.kotlin.hide
import com.yoavst.kotlin.toast
import com.yoavst.quickapps.R
import kotlin.properties.Delegates

public abstract class BaseModuleView : CardView {
    val layout: LinearLayout by Delegates.lazy { getChildAt(0) as LinearLayout }

    public constructor(context: Context) : super(context) {
        init()
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    protected open fun init() {
        LayoutInflater.from(getContext()).inflate(R.layout.desktop_view_module, this, true)
        val icon = getIcon()
        if (icon != null) {
            val bmOptions = BitmapFactory.Options()
            bmOptions.inPreferredConfig = Bitmap.Config.RGB_565
            findViewById(R.id.icon) as ImageView setImageBitmap BitmapFactory.decodeResource(getResources(), icon, bmOptions)
        }
        findViewById(R.id.title) as TextView setText getName()

    }

    abstract fun getName(): Int


    abstract fun getIcon(): Int?

    fun addSettingView(title: String, subtitle: String? = null, isChecked: Boolean? = null, callback: (check: Boolean?) -> Unit) {
        val added = LayoutInflater.from(getContext()).inflate(R.layout.desktop_view_line, layout, false) as ViewGroup
        val checkbox = added.getChildAt(0) as CheckBox
        if (isChecked != null) {
            checkbox.setChecked(isChecked)
            added.setOnClickListener { checkbox.toggle() }
            checkbox.setOnCheckedChangeListener { compoundButton, b ->
                callback(b)
            }
        } else {
            checkbox.hide()
            added.setOnClickListener { callback(null) }
        }
        ((added getChildAt 1) as ViewGroup getChildAt 0) as TextView setText title
        if (subtitle != null)
            ((added getChildAt 1) as ViewGroup getChildAt 1) as TextView setText subtitle

        layout.addView(added)
    }

    fun addSettingView(toRun: (layout: ViewGroup, checkbox: CheckBox, title: TextView, subtitle: TextView) -> Unit) {
        val added = LayoutInflater.from(getContext()).inflate(R.layout.desktop_view_line, layout, false) as ViewGroup
        layout.addView(added)
        toRun(added, added.getChildAt(0) as CheckBox, ((added getChildAt 1) as ViewGroup getChildAt 0) as TextView,
                ((added getChildAt 1) as ViewGroup getChildAt 1) as TextView)
    }

    fun addSettingView(title: Int, subtitle: Int? = null, isChecked: Boolean? = null, callback: (check: Boolean?) -> Unit) {
        addSettingView(getResources().getString(title), if (subtitle == null) null else getResources().getString(subtitle), isChecked, callback)
    }

    fun toastSuccess() {
        getContext().toast(R.string.changed_successfully)
    }

    fun toastRestartLauncher() {
        getContext().toast(R.string.restart_launcher_for_update)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (getIcon() != null) {
            val imageView = findViewById(R.id.icon) as ImageView
            (imageView.getDrawable() as BitmapDrawable).getBitmap().recycle()
        }

    }
}
