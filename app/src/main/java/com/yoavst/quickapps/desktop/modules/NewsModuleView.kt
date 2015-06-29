package com.yoavst.quickapps.desktop.modules

import android.content.Context
import android.util.AttributeSet
import com.yoavst.kotlin.toast
import com.yoavst.quickapps.R
import com.yoavst.quickapps.desktop.BaseModuleView
import com.yoavst.quickapps.tools.*


public class NewsModuleView : BaseModuleView {
    public constructor(context: Context) : super(context) {
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun init() {
        super.init()
        if (!isInEditMode()) {
            addSettingView(R.string.news_logout_feedly) {
                getContext().userId = ""
                getContext().refreshToken = ""
                getContext().accessToken = ""
                getContext().rawResponse = ""
                getContext().feed = ""
                getContext().lastUpdateTime = 0
                getContext().toast(R.string.news_logout_feedly)
            }
        }
    }

    override fun getName(): Int = R.string.news_module_name

    override fun getIcon(): Int = R.drawable.qcircle_icon_news
}