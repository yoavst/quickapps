package com.yoavst.quickapps.simon;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yoavst.quickapps.R;

/**
 * Created by Yoav.
 */
public class OppositeSemiCircleView extends ImageView {

	public OppositeSemiCircleView(Context context) {
		super(context);
		init();
	}

	public OppositeSemiCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public OppositeSemiCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		setBackground(new SemiCircleDrawable(getResources().getColor(R.color.md_deep_purple_A200), SemiCircleDrawable.Direction.RIGHT));
	}
}
