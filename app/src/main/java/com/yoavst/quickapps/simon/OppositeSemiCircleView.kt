package com.yoavst.quickapps.simon

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

import com.yoavst.quickapps.R

public class OppositeSemiCircleView : ImageView {

    public constructor(context: Context) : super(context) {
    }

    public constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    public constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    init {
        setBackground(SemiCircleDrawable(getResources().getColor(R.color.md_deep_purple_A200), SemiCircleDrawable.Direction.RIGHT))

    }
}
