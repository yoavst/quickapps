package com.lge.qcircle.template;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * The {@code QCircleTitle} class represents title views of QuickCircle.
 *
 * @author jeongeun.jeon
 */
public final class QCircleTitle extends QCircleTemplateElement {
	private final String TAG = "QCircleTitle";
	protected Context mContext = null;

	protected LinearLayout mRootView = null;
	protected TextView mTitleView = null;

    //sujin.cho
    RelativeLayout.LayoutParams params = null;
    private final float fixedTitleRatio = 0.23f; // Title height ratio


	/**
	 * creates a title bar with a text.
	 * <p>
	 * It makes a {@link android.widget.TextView} with the given text.
	 *
	 * @param title   title text for the title. <br>
	 *                If it is null, no title text will be shown but the title bar will occupy some
	 *                space.
	 * @param context {@code Activity} which has a circle view.<br>
	 *                If it is null, you might get errors when you use method of this class.
	 */
	public QCircleTitle(Context context, CharSequence title) {
		this(context, title, Color.BLACK, Color.TRANSPARENT);
	}

	/**
	 * creates a title bar with a text.
	 * <p>
	 * It makes a {@link android.widget.TextView} with the given text.
	 *
	 * @param title           title text for the title. <br>
	 *                        If it is null, no title text will be shown but the title bar will occupy some
	 *                        space.
	 * @param context         {@code Activity} which has a circle view.<br>
	 *                        If it is null, you might get errors when you use method of this class.
	 * @param titleTextColor  The color of the title
	 * @param backgroundColor The background color of the title
	 */
	public QCircleTitle(Context context, CharSequence title, int titleTextColor, int backgroundColor) {
		this(context, createTextView(context, title, titleTextColor), backgroundColor);
	}

	/**
	 * creates a title bar with the given View.
	 *
	 * @param context {@code Activity} which has a circle view.<br>
	 *                If it is null, you might get errors when you use method of this class.
	 * @param title   View for the title bar.<br>
	 *                If it is null, a empty title bar will be created.
	 */
	public QCircleTitle(Context context, View title) {
		this(context, title, Color.TRANSPARENT);
	}

	/**
	 * creates a title bar with the given View.
	 *
	 * @param context         {@code Activity} which has a circle view.<br>
	 *                        If it is null, you might get errors when you use method of this class.
	 * @param title           View for the title bar.<br>
	 *                        If it is null, a empty title bar will be created.
	 * @param backgroundColor The background color of the title
	 */
	public QCircleTitle(Context context, View title, int backgroundColor) {
		if (context != null) {
			mContext = context;
			mRootView = createRootView(context, backgroundColor);
			if (title != null) {
				if (title instanceof TextView) mTitleView = (TextView) title;
				mRootView.addView(title);
			}
		} else {
			Log.e(TAG, "Cannot create a title view. context is null");
		}
	}

	/**
	 * gets the ID of the title view.
	 *
	 * @return ID of the title view
	 */
	public int getId() {
		return R.id.title;
	}

	/**
	 * gets the view of the title.
	 *
	 * @return root of the title view
	 */
	public View getView() {
		return mRootView;
	}

	/**
	 * sets the view of the title.
	 *
	 * @param view View for title
	 * @return true if the title view is added successfully or<br>
	 * false otherwise.
	 */
	public boolean setView(View view) {
		boolean result = false;
		if (view != null && mRootView != null) {
			mRootView.removeAllViews();
			mRootView.addView(view);
			result = true;
		}
		return result;
	}

	/**
	 * sets the text of the title view.
	 *
	 * @param title title text for the title. <br>
	 *              If it is null, no title text will be shown but the title bar will occupy some
	 *              space.
	 */
	public void setText(CharSequence title) {
		if (mRootView != null && mTitleView != null)
			mTitleView.setText(title);
		else if (mRootView != null) {
			mRootView.removeAllViews();
			mRootView.addView(createTextView(mContext, title));
		}
	}

	/**
	 * sets the font size of the title text.
	 *
	 * @param size font size in pixel. <br>
	 *             If it is less or equal to 0, the font size will not change.
	 */
	public void setTextSize(float size) {
		if (mTitleView != null && size > 0) {
			mTitleView.setTextSize(size);
		}
	}

    /**
     * change a background color of title view
     * @author sujin.cho
     */
    public void setBackgroundColor(int color)
    {
        if(mRootView != null)
            mRootView.setBackgroundColor(color);
    }

    /**
     * change a text color
     * @author sujin.cho
     */
    public void setTextColor(int color)
    {
        if(mTitleView != null)
            mTitleView.setTextColor(color);
    }

	/**
	 * create a root layout.
	 * <p>
	 * The root layout will include all of contents for a title bar.
	 *
	 * @param context Activity which has this title bar
	 * @return root layout with align of horizontal-center
	 */
	private LinearLayout createRootView(Context context) {
		return createRootView(context, Color.TRANSPARENT);
	}

	/**
	 * create a root layout.
	 * <p>
	 * The root layout will include all of contents for a title bar.
	 *
	 * @param context         Activity which has this title bar
	 * @param backgroundColor The background color of the title
	 * @return root layout with align of horizontal-center
	 */
	private LinearLayout createRootView(Context context, int backgroundColor) {
		LinearLayout root = new LinearLayout(context);
		root.setId(R.id.title);
		root.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
		root.setBackgroundColor(backgroundColor);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		root.setLayoutParams(params);
		return root;
	}

	/**
	 * create a TextView.
	 *
	 * @param context Activity which has this title bar
	 * @param title   title text
	 * @return TextView with the given text
	 */
	private static TextView createTextView(Context context, CharSequence title) {
		return createTextView(context, title, Color.BLACK);
	}

	/**
	 * create a TextView.
	 *
	 * @param context   Activity which has this title bar
	 * @param title     title text
	 * @param textColor the color of the text
	 * @return TextView with the given text
	 */
	private static TextView createTextView(Context context, CharSequence title, int textColor) {
		TextView text = new TextView(context);
		text.setText(title);
		text.setBackgroundColor(Color.TRANSPARENT);
		text.setTextColor(textColor);
		text.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
		text.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		return text;
	}

    /**
     * add a title view to parent
     * This method is called in QCircle Template.
     *
     * @param parent QCircle Layout which
     *
     * @author sujin.cho
     */
    @Override
    protected void setElement(RelativeLayout parent) {
        // TODO Auto-generated method stub
        setLayoutParams();
        parent.addView(mRootView);
    }

    /*
     * set layout parameters of title view. sets position and height of the title view.
	 * @author sujin.cho
	 *
	 */
    private void setLayoutParams()
    {
        int titleAreaHeight = (int)(QCircleTemplate.getDiameter() * fixedTitleRatio);
        params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, titleAreaHeight);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
        mRootView.setLayoutParams(params);
    }

    /*
    * change a height of title view.
    * @author sujin..cho
    */
    public void setTitleHeight(float heightRatio)
    {
        if (heightRatio <= 0) // adjust the height
            heightRatio = fixedTitleRatio;
        params = (RelativeLayout.LayoutParams)mRootView.getLayoutParams();
        params.height = (int)(QCircleTemplate.getDiameter() * heightRatio);
        mRootView.setLayoutParams(params);
    }

}
