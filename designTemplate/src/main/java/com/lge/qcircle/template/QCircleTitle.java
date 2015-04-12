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

import com.lge.qcircle.utils.QCircleFeature;

/**
 * The {@code QCircleTitle} class represents title views of QuickCircle.
 *
 * @author jeongeun.jeon
 */
public final class QCircleTitle extends QCircleTemplateElement{

    private final String TAG = "QCircleTitle";

	private LinearLayout mRootView = null;
	private TextView mTitleView = null;

    //sujin.cho
    private RelativeLayout.LayoutParams mParams = null;
    private final float fixedTitleRatio = 0.23f; // Title height ratio
    private static int mFullSize = 0; // circle diameter

    private boolean useDefaultHeight = true;

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
	public QCircleTitle(Context context, String title) {
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
	public QCircleTitle(Context context, String title, int titleTextColor, int backgroundColor) {
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
            getTemplateDiameter(context);
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
	public void setText(String title) {
		if (mRootView != null && mTitleView != null)
			mTitleView.setText(title);
		else if (mRootView != null) {
			mRootView.removeAllViews();
			mRootView.addView(createTextView(mContext, title));
		}
	}

	/**
	 * Sets the font size of the title text.
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
     * Changes a background color of title view.
     * @param color  The background color.  If 0, no background. Currently must be black, with any desired alpha level.
     * @author sujin.cho
     */
    public void setBackgroundColor(int color)
    {
        if(mRootView != null)
            mRootView.setBackgroundColor(color);
    }

    /**
     * Changes a text color
     * @param color The font color.  If 0, no background. Currently must be black, with any desired alpha level.
     * @author sujin.cho
     */
    public void setTextColor(int color)
    {
        if(mTitleView != null)
            mTitleView.setTextColor(color);
    }

	/**
	 * Creates a root layout with a transparent background.
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
	 * Creates a root layout.
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
	 * Creates a TextView.
	 *
	 * @param context Activity which has this title bar
	 * @param title   title text
	 * @return TextView with the given text
	 */
	private static TextView createTextView(Context context, String title) {
		return createTextView(context, title, Color.BLACK);
	}

	/**
	 * Creates a TextView.
	 *
	 * @param context   Activity which has this title bar
	 * @param title     title text
	 * @param textColor the color of the text
	 * @return TextView with the given text
	 */
	private static TextView createTextView(Context context, String title, int textColor) {
		TextView text = new TextView(context);
		text.setText(title);
		text.setBackgroundColor(Color.TRANSPARENT);
		text.setTextColor(textColor);
		text.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
		text.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		return text;
	}

    /**
     * Adds a title view to parent
     * <p>
     * This method is called in QCircleTemplate. First, the methods sets layout parameters for the title.
     * Next, the method adds the title to parent. Once the title is added, UIs in the parent needs to be adjusted for the new component.
     *
     * @param parent QCircle Layout which
     *
     * @author sujin.cho
     */
    @Override
    protected void addTo(RelativeLayout parent, RelativeLayout content) {

        if((mRootView != null) && (parent != null)) {

            if(useDefaultHeight) {
                setLayoutParams(fixedTitleRatio);
            }
            parent.addView(mRootView);
            adjustLayout(content);
        }
    }

    private void adjustLayout(RelativeLayout content)
    {
        if(content != null) {
            RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(
                    content.getLayoutParams().width, content.getLayoutParams().height);
            contentParams.addRule(RelativeLayout.BELOW, mRootView.getId());
            content.setLayoutParams(contentParams);
        }
    }


    private void setLayoutParams(float heightRatio)
    {
        int titleAreaHeight = (int)(mFullSize * heightRatio);
        mParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, titleAreaHeight);
        mParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
        mRootView.setLayoutParams(mParams);
    }


    /**
    * Changes a height of title view.
    * @param heightRatio ratio of the portion of title area respect to the circle diameter.
    * @author sujin..cho
    */
    public void setTitleHeight(float heightRatio)
    {
        useDefaultHeight = false;
        setLayoutParams(heightRatio);
    }


    /**
     * Locates the circle on the correct position. The correct position depends on phone model.
     * <p>
     * @author sujin.cho
     */
    private void getTemplateDiameter(Context context)
    {
        if(context != null) {
            if (!QCircleFeature.isQuickCircleAvailable(context)) {
                Log.i(TAG, "Quick Circle case is not available");
                return;
            }
            // circle size
            int id = context.getResources().getIdentifier(
                    "config_circle_diameter", "dimen", "com.lge.internal");
            mFullSize = context.getResources().getDimensionPixelSize(id);
        }
        else
        {

        }
    }
}
