package com.lge.qcircle.template;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

/**
 * The {@code QCircleBackButton} class represents back buttons of QuickCircle.
 *
 * @author jeongeun.jeon
 */
public final class QCircleBackButton extends QCircleTemplateElement{
	private final String TAG = "QCircleBackButton";
	private OnClickListener mListener;
	private ImageView mBtnContent = null;
	private int		mButtonHeight = 0;
	private Context mContext = null;
	private boolean isDark = false;
	
	private static float PADDING_RATIO = 0.35f;
    //sujin.cho
    private final float fixedButtonRatio = 0.23f;
    RelativeLayout.LayoutParams params = null;



    /**
     * creates a back button.
     *
     * @param context {@code Activity} which has a circle view.<br>
     * <b>If it is null, you might get errors when you use method of this class.</b>
     */
    public QCircleBackButton(Context context) {
        mContext = context;
        mButtonHeight = (int)(fixedButtonRatio * QCircleTemplate.getDiameter());
        if (!setButton())
            Log.d(TAG, "Cannot create a button. Context is null.");
    }

	/**
	 * creates a back button.
	 *
	 * @param context {@code Activity} which has a circle view.<br>
	 * <b>If it is null, you might get errors when you use method of this class.</b>
	 */
	public QCircleBackButton(Context context, int height) {
		this(context, height, null);
	}

	/**
	 * creates a back button.
	 *
	 * @param context {@code Activity} which has a circle view.<br>
	 * <b>If it is null, you might get errors when you use method of this class.</b>
	 * @param listener Listener on click
	 */
	public QCircleBackButton(Context context, int height, OnClickListener listener) {
		mContext = context;
		mListener = listener;
		mButtonHeight = height;
		if (!setButton())
			Log.d(TAG, "Cannot create a button. Context is null.");
	}

	/**
	 * sets the layout of the button.
	 *
	 * @return true if a button is configured successfully or
	 * false otherwise.
	 */
	private boolean setButton() {
		boolean result = false;
		if (mContext != null) {
			mBtnContent = new ImageView(mContext);
			mBtnContent.setPadding(0,(int)(mButtonHeight*PADDING_RATIO), 0, (int)(mButtonHeight*PADDING_RATIO));
			
			// set attributes
			mBtnContent.setId(R.id.backButton);
			setTheme();
			mBtnContent.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mListener != null) mListener.onClick(v);
					if (mContext != null) {
						try {
							((Activity) mContext).finish();
						} catch (ClassCastException e) {
							Log.e(TAG, "Cannot finish the Application: the context is not an Activity.");
						}
					}
				}
			});
			result = true;
		}
		return result;
	}

    /**
     * sets theme of the back button.
     * @author Yoav Sternberg
     */
	private void setTheme() {
		mBtnContent.setImageResource(isDark ? R.drawable.backover_dark : R.drawable.backover);
		mBtnContent.setBackgroundResource(isDark ? R.drawable.back_button_background_dark : R.drawable.back_button_background);
	}

    /**
     * uses dark theme for the back button.<P>
     *
     * @param isDark  flag which indicates whether dark theme is used or not.
     * @author Yoav Sternberg
     */
	public void isDark(boolean isDark) {
		this.isDark = isDark;
		setTheme();
	}

	/**
	 * gets the view of the button.
	 *
	 * @return button view
	 */
	public View getView() {
		return mBtnContent;
	}

	/**
	 * gets the ID of the button view.
	 *
	 * @return ID of the button view
	 */
	public int getId() {
		return R.id.backButton;
	}

	/**
	 * sets the background of the button transparent.
	 */
	public void setBackgroundTransparent() {
		if (mBtnContent != null)
			mBtnContent.setBackgroundResource(R.drawable.back_button_background_semi_transparent);
	}

    /**
     * @author sujin.cho
     */
    @Override
    public void setElement(RelativeLayout parent) {
        // TODO Auto-generated method stub
        setLayoutParams();
        parent.addView(mBtnContent);
    }

    private void setLayoutParams()
    {
        //int buttonAreaHeight = (int) (mFullSize * fixedButtonRatio);
        int buttonAreaHeight = (int)(1046 * 0.23);

        // add a button into the bottom of the circle layout
        params = new RelativeLayout.LayoutParams(1046,buttonAreaHeight);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
        mBtnContent.setLayoutParams(params);
        //mCircleLayout.addView(buttonView.getView(), params);
    }

}
