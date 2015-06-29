package com.yoavst.quickapps.desktop.modules

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.BaseModuleView


public class MusicModuleView : BaseModuleView {
    public constructor(context: Context) : super(context) {
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun init() {
        super.init()
        if (!isInEditMode()) {
            addSettingView(R.string.music_listener_title, R.string.music_listener_subtitle) {
                getContext().startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
            }
        }
    }

    override fun getName(): Int = R.string.music_module_name

    override fun getIcon(): Int = R.drawable.qcircle_icon_music
}