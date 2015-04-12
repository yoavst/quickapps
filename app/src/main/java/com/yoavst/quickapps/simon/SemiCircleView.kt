package com.yoavst.quickapps.simon

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

import com.yoavst.quickapps.R

/**
 * Created by Yoav.
 */
public class SemiCircleView : ImageView {

    public constructor(context: Context) : super(context) {
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    init {
        setBackground(SemiCircleDrawable(getResources().getColor(R.color.md_pink_A200), SemiCircleDrawable.Direction.LEFT))
    }
}
