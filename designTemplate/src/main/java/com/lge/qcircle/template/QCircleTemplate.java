package com.lge.qcircle.template;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lge.qcircle.utils.QCircleFeature;

/**
 * The {@code QCircleTemplate} class provides design templates for LG QuickCircle.
 * <p/>
 * There are 5 templates: <br>
 * <ul>
 * <li>empty content</li>
 * <li>content with a horizontal sidebar</li>
 * <li>content with a vertical sidebar</li>
 * <li>content with 2 topbars</li>
 * <li>content with 2 sidebars</li>
 * </ul>
 * In order to get the layout, see {@link com.lge.qcircle.template.TemplateType}.<br>
 * In addition, you can add a title or a back button into each layout described above.
 * <p/>
 * This class supports APIs for setting layout, changing attributes of Views in the layout, and
 * setting fullscreen intent.
 *
 * @author jeongeun.jeon
 * @see com.lge.qcircle.template.TemplateType
 */
public class QCircleTemplate {

	// constants
	protected static final String TAG = "QCircleTemplate";
	protected static final int EXTRA_ACCESSORY_COVER_OPENED = 0;
	protected static final int EXTRA_ACCESSORY_COVER_CLOSED = 1;
	protected static final String EXTRA_ACCESSORY_COVER_STATE = "com.lge.intent.extra.ACCESSORY_COVER_STATE";
	protected static final String ACTION_ACCESSORY_COVER_EVENT = "com.lge.android.intent.action.ACCESSORY_COVER_EVENT";

	// strings for special devices
	protected static final String DEVICE_G3 = "g3";
	protected static final String DEVICE_T6 = "tiger6";

	// environments
	protected Context mContext = null;
	protected BroadcastReceiver mReceiver = null;
	protected Intent mFullscreenIntent = null;
	protected IntentCreatorAsync mAsyncCreator = null;

	// Views
	protected TemplateType mLayoutType = TemplateType.CIRCLE_EMPTY;
	protected FrameLayout mRootLayout = null;
	protected RelativeLayout mCircleLayout = null;
	protected RelativeLayout mContent = null;
	protected QCircleBackButton mBackButton = null;
	protected QCircleTitle mTitle = null;

	// layout values
	private static int mFullSize = 0; // circle diameter
	private static int mTopOffset = 0; // top offset of circle
	private static int mYpos = 0;

	/**
	 * @deprecated
	 */
	private final float fixedButtonRatio = 0.23f; // Button height ratio
	/**
	 * @deprecated
	 */
	private final float fixedTitleRatio = 0.23f; // Title height ratio

	/**
	 * creates a template with an empty content.
	 *
	 * @param context Activity which has the template. <br>
	 *                It should be an Activity because the template will close the Activity when the
	 *                cover is opened.<br>
	 *                If it is null, other APIs will not work and report errors.
	 */
	public QCircleTemplate(Context context) {
		this(context, TemplateType.CIRCLE_EMPTY);
	}

	/**
	 * creates a template with the given template type.
	 *
	 * @param context Activity which has the template. <br>
	 *                It should be an Activity because the template will close the Activity when the
	 *                cover is opened.<br>
	 *                If it is null, other APIs will not work and report errors.
	 * @param type    type of the design template you want to use.<br>
	 *                If it is null, an empty content template will be used.
	 * @see com.lge.qcircle.template.TemplateType
	 */
	public QCircleTemplate(Context context, TemplateType type) {
		mContext = context;
		if (type == null)
			type = TemplateType.CIRCLE_EMPTY;
		setTemplateType(type); // set layout style
	}

	/**
	 * creates a template with the given layout.
	 *
	 * @param context    Activity which has the template. <br>
	 *                   It should be an Activity because the template will close the Activity when the
	 *                   cover is opened.<br>
	 *                   If it is null, other APIs will not work and report errors.
	 * @param templateId ID of the layout to use
	 */
	public QCircleTemplate(Context context, int templateId) {
		mContext = context;
		if (templateId <= 0) {
			templateId = R.layout.qcircle_empty;
		}
		loadCustomTemplate(templateId);
	}

	/**
	 * sets the intent running in fullscreen when the cover is opened.
	 *
	 * @param intent intent to run the fullscreen Activity. <br>
	 *               If it is null, nothing will be happened when the cover is opened.<br>
	 *               You can set this as null when you clean up the existing intent.
	 */
	public void setFullscreenIntent(Intent intent) {
		if (intent == null) {
			Log.w(TAG, "The given intent is null");
		}
		mFullscreenIntent = intent;
		mAsyncCreator = null;
	}

	/**
	 * sets the intent running in fullscreen when the cover is opened.
	 *
	 * @param creatorAsync will be called only when the intent is needed.
	 */
	public void setFullscreenIntent(IntentCreatorAsync creatorAsync) {
		mFullscreenIntent = null;
		mAsyncCreator = creatorAsync;
	}

	/**
	 * gets the root view of the template layout.
	 * <p/>
	 *
	 * @return root view of the layout.
	 */
	public View getView() {
		setQuickCircleWindowParam();
		return mRootLayout;
	}

	/**
	 * @param title text for the title. <br>
	 *              If it is null, no title text will be shown but the title bar will occupy some
	 *              space.
	 * @deprecated sets a title.
	 * <p/>
	 * It creates a title bar on the top of the layout.
	 * <p/>
	 * Note that this method does not create a title bar when the title bar already exists. It
	 * changes the text of the existing title bar.
	 */
	public void setTitle(String title) {
		setTitle(title, fixedTitleRatio);
	}

	/**
	 * @param title       text for the title. <br>
	 *                    If it is null, no title text will be shown but the title bar will occupy some
	 *                    space.
	 * @param heightRatio ratio of the title bar. <br>
	 *                    If it is less or equal to 0, the height ratio of the title bar will be 0.2 of
	 *                    QuickCircle diameter.
	 * @deprecated sets a title with the given height ratio.
	 * <p/>
	 * It creates a title bar on the top of the layout. The height of the title bar depends on
	 * {@code heightRatio}.
	 * <p/>
	 * Note that this method does not create a title bar when the title bar already exists. It
	 * changes the text and the height of the existing title bar.
	 */
	public void setTitle(String title, float heightRatio) {
		setTitle(title, heightRatio, Color.BLACK, Color.TRANSPARENT);
	}

	/**
	 * @param title           text for the title. <br>
	 *                        If it is null, no title text will be shown but the title bar will occupy some
	 *                        space.
	 * @param heightRatio     ratio of the title bar. <br>
	 *                        If it is less or equal to 0, the height ratio of the title bar will be 0.2 of
	 *                        QuickCircle diameter.
	 * @param textColor       The color of the title
	 * @param backgroundColor The background color of the title
	 * @deprecated sets a title with the given height ratio and the given title color and background color.
	 * <p/>
	 * It creates a title bar on the top of the layout. The height of the title bar depends on
	 * {@code heightRatio}.
	 * <p/>
	 * Note that this method does not create a title bar when the title bar already exists. It
	 * changes the text and the height of the existing title bar.
	 */
	public void setTitle(String title, float heightRatio, int textColor, int backgroundColor) {
		if (mTitle == null) {
			if (mContext != null)
				mTitle = new QCircleTitle(mContext, title, textColor, backgroundColor);
			else {
				Log.w(TAG, "Cannot create the title: context is null");
				return;
			}
			addTitleView(mTitle, heightRatio);
		} else { // change the string only
			mTitle.setText(title);
			changeTitleViewHeight(heightRatio);
		}
	}

	/**
	 * @param title           text for the title. <br>
	 *                        If it is null, no title text will be shown but the title bar will occupy some
	 *                        space.
	 * @param textColor       The color of the title
	 * @param backgroundColor The background color of the title
	 * @deprecated sets a title with the default height ratio and the given title color and background color.
	 * <p/>
	 * It creates a title bar on the top of the layout. The height of the title bar depends on
	 * {@code heightRatio}.
	 * <p/>
	 * Note that this method does not create a title bar when the title bar already exists. It
	 * changes the text and the height of the existing title bar.
	 */
	public void setTitle(String title, int textColor, int backgroundColor) {
		setTitle(title, fixedTitleRatio, textColor, backgroundColor);
	}

	/**
	 * @param titleView   View to be a title
	 * @param heightRatio ratio of the title bar. <br>
	 *                    If it is less or equal to 0, the height ratio of the title bar will be 0.2 of
	 *                    QuickCircle diameter.
	 * @deprecated sets a title as the given view with the given height ratio.
	 * <p/>
	 * It creates a title bar on the top of the layout. The content of the title bar is
	 * {@code titleView} and the height of the title bar depends on {@code heightRatio}.
	 * <p/>
	 * Note that this method does not create a title bar when the title bar already exists. It
	 * changes the content and the view of the existing title bar.
	 */
	public void setTitle(View titleView, float heightRatio) {
		if (mTitle == null) { // create a Title
			if (mContext != null)
				mTitle = new QCircleTitle(mContext, titleView);
			else {
				Log.w(TAG, "Cannot create the title: context is null");
				return;
			}
			addTitleView(mTitle, heightRatio);
		} else {
			Log.i(TAG, "Title view is updated by user.");
			if (!mTitle.setView(titleView)) {
				Log.w(TAG, "Cannot set the view as a title.");
			} else {
				changeTitleViewHeight(heightRatio);
			}
		}
	}

	/**
	 * @deprecated sets a back button.
	 * <p/>
	 * It creates a back button on the bottom of the layout.
	 * <p/>
	 * You do not need to implement an {@code onClickListener} for the button, because it already
	 * has one.<br>
	 * Note that this method does not create a button when the button already exists.
	 */
	public void setBackButton() {
		setBackButton(null);
	}

	/**
	 * @param onClickListener Callback for back button.
	 *                        <b>should be used for closing objects, like camera</b>
	 * @deprecated sets a back button with callback.
	 * <p/>
	 * It creates a back button on the bottom of the layout.
	 * <p/>
	 * You do not need to implement an {@code onClickListener} for the button, because it already
	 * has one.<br>
	 * Note that this method does not create a button when the button already exists.
	 */
	public void setBackButton(View.OnClickListener onClickListener) {
		if (mBackButton == null) {
			if (mContext != null)
				mBackButton = new QCircleBackButton(mContext, (int) (mFullSize * fixedButtonRatio), onClickListener);
			else {
				Log.e(TAG, "Cannot create the back button: context is null");
				return;
			}
			addBackButtonView(mBackButton);
		}
	}

	/**
	 * @param isDark Is dark theme
	 * @author Yoav Sternberg
	 * @deprecated Set the theme to the back button. Default is light.
	 */
	public void setBackButtonTheme(boolean isDark) {
		if (mBackButton != null) {
			mBackButton.isDark(isDark);
		}
	}

	/**
	 * gets a layout with the given id.
	 * <p/>
	 * This method is useful when you want to add or modify Views of the template.<br>
	 * Every content or sidebar is a {@code RelativeLayout}.
	 *
	 * @param id id of the layout to retrieve. Use {@link com.lge.qcircle.template.TemplateTag}.<br>
	 * @return a {@code RelativeLayout} whose ID is identical to {@code id}.<br>
	 * or null if there is no View with the given ID.
	 */
	public RelativeLayout getLayoutById(int id) {
		RelativeLayout result = null;
		if (mContent != null && id > 0) {
			result = (RelativeLayout) mContent.findViewById(id);
		}
		return result;
	}

	/**
	 * changes the ratio of the first sidebar.
	 * <p/>
	 * Some design templates have sidebars which is re-sizable. Note that only the first sidebar can
	 * be re-sized even if the template has more than 1 sidebars.<br>
	 * This method changes the size of the first sidebar with the ratio comparing to the full
	 * content layout.<br>
	 * If you add a title bar or a back button after calling this method, the size of the sidebar
	 * will not change. Therefore some UI components might be hidden by the title bar or a back
	 * button. <br>
	 * Call this method after adding a title bar and a back button to prevent the situation.
	 *
	 * @param weight ratio of the first sidebar. <br>
	 *               The valid ranage is (0,1). (cannot be 0 or 1).
	 * @return true, if the size is changed successfully or<br>
	 * false, otherwise.
	 */
	public boolean setSidebarRatio(float weight) {
		boolean result = false;
		if (mContent != null) { // check validation
			if (weight < 0 || weight > 1) { // check validation
				Log.i(TAG, "content rate should be in range (0,1): current = " + weight);
			} else {
				// get side1
				View firstContent = (View) mContent.findViewById(R.id.side1);
				if (mLayoutType == TemplateType.CIRCLE_COMPLEX) {
					firstContent = (View) firstContent.getParent();
				}
				if (firstContent != null) {
					int parentSize = mFullSize;
					if (mLayoutType == TemplateType.CIRCLE_VERTICAL) { // adjust width
						LayoutParams params = firstContent.getLayoutParams();
						params.width = (int) (parentSize * weight);
						firstContent.setLayoutParams(params);

					} else if (mLayoutType == TemplateType.CIRCLE_HORIZONTAL // adjust height
							|| mLayoutType == TemplateType.CIRCLE_COMPLEX) {
						parentSize = (int) (mFullSize * (1 - 0.2 * ((mBackButton != null ? 1 : 0) + (mTitle != null ? 1
								: 0))));
						LayoutParams params = firstContent.getLayoutParams();
						params.height = (int) (parentSize * weight);
						firstContent.setLayoutParams(params);
					}
					result = true;
				} else
					Log.w(TAG, "There is no first sidebar in this layout");
			}
		} else
			Log.w(TAG, "No content layout. please set default content layout");
		return result;
	}

	/**
	 * @param image              background image
	 * @param overwiteButtonArea flag for clear background of the back button if it exists.<br>
	 *                           The default background of a back button is light gray. You should clear the
	 *                           background color of a back button when you want to use full-layout background.<br>
	 *                           Set this flag in that case.
	 * @see #setBackgroundColor(int, boolean)
	 * @deprecated sets the background of the template as the given image.
	 * <p/>
	 * The background affects all the layouts including a title bar and a back button.
	 */
	public void setBackgroundDrawable(Drawable image, boolean overwiteButtonArea) {
		if (mCircleLayout != null)
			mCircleLayout.setBackground(image);
		if (overwiteButtonArea && mBackButton != null)
			mBackButton.setBackgroundTransparent();
		// if (overwiteButtonArea && mTitle != null)
		// mTitle.setBackgroundTransparent();
	}

	/**
	 * sets the background of the template as the given image.
	 * <p/>
	 * The background affects all the layouts including a title bar and a back button.
	 *
	 * @param image background image
	 * @see #setBackgroundColor(int, boolean)
	 */
	public void setBackgroundDrawable(Drawable image) {
		if (mCircleLayout != null)
			mCircleLayout.setBackground(image);
	}

	/**
	 * @param color              background color
	 * @param overwiteButtonArea flag for clear background of the back button if it exists.<br>
	 *                           The default background of a back button is light gray. You should clear the
	 *                           background color of a back button when you want to use full-layout background.<br>
	 *                           Set this flag in that case.
	 * @see #setBackgroundDrawable(android.graphics.drawable.Drawable, boolean)
	 * @deprecated sets the background of the template as the given color.
	 * <p/>
	 * The background affects all the layouts including a title bar and a back button.
	 */
	public void setBackgroundColor(int color, boolean overwiteButtonArea) {
		if (mCircleLayout != null)
			mCircleLayout.setBackgroundColor(color);
		if (overwiteButtonArea && mBackButton != null)
			mBackButton.setBackgroundTransparent();
	}

	/**
	 * sets the background of the template as the given color.
	 * <p/>
	 * The background affects all the layouts including a title bar and a back button.
	 *
	 * @param color background color
	 * @see #setBackgroundDrawable(android.graphics.drawable.Drawable, boolean)
	 */
	public void setBackgroundColor(int color) {
		if (mCircleLayout != null)
			mCircleLayout.setBackgroundColor(color);
	}

	/**
	 * @param size font size in pixel
	 * @deprecated sets the font size of the title.
	 * <p/>
	 */
	public void setTitleTextSize(float size) {
		if (mTitle != null) {
			mTitle.setTextSize(size);
		}
	}

	/**
	 * initializes the layout of the circle window.
	 * <p/>
	 * It loads a template layout from the xml file and retrieves the root view (actually a
	 * {@code FrameLayout} and the content view(a {@code RelativeLayout}).
	 *
	 * @param layoutView View that represents a layout. It should be a root view of the layout.<br>
	 *                   If it is null, setting layout will fail.
	 */
	private void initLayout(View layoutView) {
		if (layoutView != null) {
			mRootLayout = (FrameLayout) layoutView.findViewById(R.id.root);
			mCircleLayout = (RelativeLayout) layoutView.findViewById(R.id.circlelayout);
			mContent = (RelativeLayout) layoutView.findViewById(R.id.content);

		} else {
			Log.e(TAG, "Cannot set the layout: root view is null");
		}
	}

	/**
	 * @param heightRatio ratio of the title bar.
	 * @deprecated changes the height of the title.<P>
	 * If there is no title bar on the layout, no happens.
	 */
	private void changeTitleViewHeight(float heightRatio) {
		if (mTitle != null) {
			if (heightRatio <= 0) // adjust the height
				heightRatio = fixedTitleRatio;
			int titleAreaHeight = (int) (mFullSize * heightRatio);
			// add a title into the top of the circle layout
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, titleAreaHeight);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
			mTitle.getView().setLayoutParams(params);
			adjustContentLayout();
		}
	}

	/**
	 * @param titleView   title view to be added. <br>
	 *                    If it is null, the circle layout will not change.
	 * @param heightRatio ratio of the title bar. <br>
	 *                    If it is less or equal to 0, the height ratio of the title bar will be 0.2 of
	 *                    QuickCircle diameter.
	 * @deprecated adds a title view to the layout.
	 * <p/>
	 * It is called by {@link #setTitle(String)} to adjust the circle layout. The title view is
	 * added on the top of the layout and the content window will be on the below of the title view.
	 */
	private void addTitleView(QCircleTitle titleView, float heightRatio) {
		if (mCircleLayout != null) {
			if (heightRatio <= 0) // adjust the height
				heightRatio = fixedTitleRatio;
			int titleAreaHeight = (int) (mFullSize * heightRatio);
			// add a title into the top of the circle layout
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, titleAreaHeight);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
			mCircleLayout.addView(titleView.getView(), 0, params);
			adjustContentLayout(); // change the size of the content because of adding a title
		}
	}

	/**
	 * @param buttonView button view to be added. <br>
	 *                   If it is null, the circle layout will not change.
	 * @deprecated adds a button view to the layout.
	 * <p/>
	 * It is called by {@link com.lge.qcircle.template.QCircleTemplate#setBackButton()} to adjust the circle layout. The
	 * button view is added on the bottom of the layout and the content window will be on the top of
	 * the button view.
	 */
	private void addBackButtonView(QCircleBackButton buttonView) {
		if (mCircleLayout != null) {
			int buttonAreaHeight = (int) (mFullSize * fixedButtonRatio);
			// add a button into the bottom of the circle layout
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mFullSize,
					buttonAreaHeight);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
			mCircleLayout.addView(buttonView.getView(), params);
			adjustContentLayout(); // change the size of the content because of adding a button
		}
	}

	/**
	 * @deprecated adjust the circle layout when a title view or a button view is added.
	 * <p/>
	 * It is called by {@link #addBackButtonView(QCircleBackButton)).
	 * It changes the relative position of the content window.
	 */
	protected void adjustContentLayout() {
		// get current size of the content
		RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(
				mContent.getLayoutParams().width, mContent.getLayoutParams().height);
		// set relative layout parameters
		if (mBackButton != null) {
			contentParams.addRule(RelativeLayout.ABOVE, mBackButton.getId());
		}
		if (mTitle != null) {
			contentParams.addRule(RelativeLayout.BELOW, mTitle.getId());
		}
		// update layout parameters
		mContent.setLayoutParams(contentParams);
	}

	/**
	 * locates the circle on the correct position. The correct position depends on phone model.
	 * <p/>
	 */
	protected void setCircleLayout() {
		// 1. get circle size and Y offset
		// circle size
		initCircleLayoutParam();
		// 2. adjust the circle layout for the model
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mFullSize, mFullSize);
		// circle image
		View circleView = (View) mRootLayout.findViewById(R.id.circle);
		String device = android.os.Build.DEVICE;
		if (circleView != null) {
			if (device.equalsIgnoreCase(DEVICE_G3) || device.equalsIgnoreCase(DEVICE_T6)) {
				params.topMargin = 0;
			} else {
				params.topMargin = mTopOffset;
			}
			params.gravity = Gravity.CENTER_HORIZONTAL;
			circleView.setLayoutParams(params);

		} else {
			Log.w(TAG, "Cannot found circle image");
		}
		// over-circle layout
		circleView = (View) mRootLayout.findViewById(R.id.circlelayout);
		if (circleView != null) {
			circleView.setLayoutParams(params);
		} else {
			Log.w(TAG, "Cannot found circle layout");
		}
	}

	/**
	 * locates the circle on the correct position. The correct position depends on phone model.
	 * <p/>
	 *
	 * @author sujin.cho
	 */
	private boolean initCircleLayoutParam() {
		if (!QCircleFeature.isQuickCircleAvailable(mContext)) {
			Log.i(TAG, "Quick Circle case is not available");
			return false;
		}
		// circle size
		mFullSize = QCircleFeature.getTemplateDiameter(mContext);
		// y position (in G3, y position = y offset)
		mYpos = QCircleFeature.getYPosition(mContext);
		// adjust Y offset for the model
		int height = QCircleFeature.getWindowHeight(mContext);
		mTopOffset = mYpos + ((height - mFullSize) / 2);
		return true;
	}

	/**
	 * sets the design template.
	 * <p/>
	 *
	 * @param type design template type to be set
	 */
	protected void setTemplateType(TemplateType type) {
		mLayoutType = type;
		if (mContext != null) {
			View layoutView = null;
			switch (mLayoutType) {
				case CIRCLE_EMPTY:
					Log.d("test", "class name = "
							+ mContext.getApplicationContext().getPackageName());
					layoutView = ((Activity) mContext).getLayoutInflater().inflate(
							R.layout.qcircle_empty, null);
					break;
				case CIRCLE_COMPLEX:
					layoutView = ((Activity) mContext).getLayoutInflater().inflate(
							R.layout.qcircle_complex, null);
					break;
				case CIRCLE_HORIZONTAL:
					layoutView = ((Activity) mContext).getLayoutInflater().inflate(
							R.layout.qcircle_horizontal, null);
					break;
				case CIRCLE_VERTICAL:
					layoutView = ((Activity) mContext).getLayoutInflater().inflate(
							R.layout.qcircle_vertical, null);
					break;
				case CIRCLE_SIDEBAR:
					layoutView = ((Activity) mContext).getLayoutInflater().inflate(
							R.layout.qcircle_sidebar, null);
					break;
			}
			initLayout(layoutView); // update root layout with a new template
			setCircleLayout(); // set the circle layout
		} else {
			Log.w(TAG, "Cannot set the layout. Context is null");
		}

	}

	/**
	 * registers cover event broadcasts.
	 * <p/>
	 * The a cover-closed event will make the circle shown and a cover-opened event will make the
	 * circle hidden after you invoke this method. The full screen intent will starts if you set a
	 * fullscreen intent by calling {@link #setFullscreenIntent(android.content.Intent)}.
	 */
	public void registerIntentReceiver() {
		if (mContext != null) {
			mReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					// Log.d(TAG, "onReceive: " + intent.getAction());
					String action = intent.getAction();
					if (action == null) {
						return;
					}
					if (!QCircleFeature.isQuickCircleAvailable(mContext)) {
						Log.i(TAG, "Quick Circle case is not available");
						return;
					}
					// Receives a LG QCirle intent for the cover event
					if (ACTION_ACCESSORY_COVER_EVENT.equals(action)) {
						// Gets the current state of the cover
						int quickCoverState = intent.getIntExtra(EXTRA_ACCESSORY_COVER_STATE,
								EXTRA_ACCESSORY_COVER_OPENED);
						if (quickCoverState == EXTRA_ACCESSORY_COVER_CLOSED) { // closed
							//setQuickCircleWindowParam();
						} else if (quickCoverState == EXTRA_ACCESSORY_COVER_OPENED) { // opened
							if (mFullscreenIntent != null && mContext != null) {
								mContext.startActivity(mFullscreenIntent);
							} else if (mContent != null && mAsyncCreator != null) {
								Intent launching = mAsyncCreator.getIntent();
								if (launching != null) {
									try {
										mContext.startActivity(launching);
									} catch (ActivityNotFoundException e) {
										// Package does not exist, ignore.
										e.printStackTrace();
									}
								}
							}
							if (mContext instanceof Activity) {
								((Activity) mContext).finish();
							}
						}
					}
				}
			};
			IntentFilter filter = new IntentFilter();
			filter.addAction(ACTION_ACCESSORY_COVER_EVENT);
			// Register a broadcast receiver with the system
			mContext.registerReceiver(mReceiver, filter);
		}
	}

	/**
	 * un-registers a default broadcast receiver.<P>
	 * It must be called when {@link #registerIntentReceiver) has been called.
	 *
	 * @author Yoav Sternberg
	 */
	public void unregisterReceiver() {
		try {
			mContext.unregisterReceiver(mReceiver);
		} catch (Exception ignored) {
			// Receiver not registered
		}
	}

	/**
	 * makes the circle shown even if the screen is locked.
	 */
	private void setQuickCircleWindowParam() {
		if (mContext != null && mContext instanceof Activity) {
			//only for Activity extends Activity. does not work for ActionBarActivity....
			if (!((Activity) mContext).getWindow().hasFeature(Window.FEATURE_NO_TITLE))
				((Activity) mContext).requestWindowFeature(Window.FEATURE_NO_TITLE);
			Window win = ((Activity) mContext).getWindow();
			if (win != null) {
				// Show the sample application view on top
				win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
								| WindowManager.LayoutParams.FLAG_FULLSCREEN
								| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				);
			}
		}
	}

	/**
	 * @deprecated
	 */
	protected boolean setLayoutById(int id, View view) {
		boolean result = false;
		if (mContent != null) {
			View targetView = getLayoutById(id);
			if (targetView != null) {
				result = true;
				LayoutParams params = targetView.getLayoutParams();
				view.setId(targetView.getId());
				ViewParent parent = targetView.getParent();
				if (parent instanceof LinearLayout) {
					LinearLayout layout = (LinearLayout) parent;
					layout.removeView(targetView);
					layout.addView(view, params);
				} else { // maybe RelativeLayout
					RelativeLayout layout = (RelativeLayout) parent;
					layout.removeView(targetView);
					layout.addView(view, params);
				}
			}
		}
		return result;
	}

	/**
	 * loads external layout created by users (or user).
	 * <p/>
	 *
	 * @param templateId ID of layout. <br>
	 *                   It should be larger than 0.
	 */
	protected void loadCustomTemplate(int templateId) {
		if (mContext != null && templateId > 0) {
			View layoutView = ((Activity) mContext).getLayoutInflater().inflate(templateId,
					null);
			if (layoutView == null)
				Log.w(TAG, "Cannot set the custom layout: " + templateId);
			else {
				initLayout(layoutView);
				setCircleLayout();
			}
		} else {
			Log.w(TAG, "Cannot set the custom layout. Context is null");
		}
	}

	/**
	 * @author Yoav Sternberg
	 */
	public static interface IntentCreatorAsync {
		Intent getIntent();
	}

	/**
	 * Adds a UI element to a template.
	 *
	 * @param element UI element.
	 *                The element should extend QCricleTemplateElement abstract class.
	 * @author sujin.cho
	 */
	public void addElement(QCircleTemplateElement element) {
		element.addTo(mCircleLayout, mContent);
		//remove it later....
		if (element instanceof QCircleBackButton) mBackButton = (QCircleBackButton) element;
		else if (element instanceof QCircleTitle) mTitle = (QCircleTitle) element;
	}

	/**
	 * Returns a diameter of the Quick Circle. This can be used to fit a layout in the Quick Circle window.
	 *
	 * @return diameter if the "config_circle_diameter" has a value.
	 * -1 if the "config_circle_diameter" is not loaded.
	 */
	public int getDiameter() {
		if (mFullSize == 0) {
			if (initCircleLayoutParam() != true) return -1;
		}
		return mFullSize;
	}

	/**
	 * Returns a vertical position of the Quick Circle from the top. This can be used to properly locate a layout in the Quick Circle window.
	 *
	 * @return a position from the top if required configs have values.
	 * -1 if required configs are not loaded.
	 */
	public int getYpos() {
		if (mTopOffset == 0) {
			if (initCircleLayoutParam() != true) return -1;
		}
		return mTopOffset;
	}
}
