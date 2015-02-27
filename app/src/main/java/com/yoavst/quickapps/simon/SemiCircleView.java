package com.yoavst.quickapps.simon;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.yoavst.quickapps.R;

/**
 * Created by Yoav.
 */
public class SemiCircleView extends ImageView {

	public SemiCircleView(Context context) {
		super(context);
		init();
	}

	public SemiCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SemiCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		setBackground(new SemiCircleDrawable(getResources().getColor(R.color.md_pink_A200), SemiCircleDrawable.Direction.LEFT));
	}
}
