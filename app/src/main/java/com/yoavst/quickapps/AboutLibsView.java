package com.yoavst.quickapps;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.ui.adapter.LibsRecyclerViewAdapter;
import com.yoavst.mashov.AsyncJob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Yoav.
 */
public class AboutLibsView extends FrameLayout {
	private RecyclerView mRecyclerView;
	private LibsRecyclerViewAdapter mAdapter;

	private ArrayList<Library> libraries;

	private boolean autoDetect = true;
	private boolean sort = true;
	private boolean animate = true;

	private boolean showLicense = false;
	private boolean showLicenseDialog = true;
	private boolean showVersion = false;

	private Boolean aboutShowIcon = null;
	private Boolean aboutShowVersion = null;
	private String aboutDescription = null;

	private HashMap<String, HashMap<String, String>> libraryModification;

	private Comparator<Library> comparator;

	public AboutLibsView(Context context) {
		super(context);
		init();
	}

	public AboutLibsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AboutLibsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			LayoutInflater.from(getContext()).inflate(R.layout.fragment_opensource, this, true);
			// init CardView
			mRecyclerView = (RecyclerView) findViewById(R.id.cardListView);
			mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
			mRecyclerView.setItemAnimator(new DefaultItemAnimator());
			mAdapter = new LibsRecyclerViewAdapter(getContext(), showLicense, showLicenseDialog, showVersion);
			mRecyclerView.setAdapter(mAdapter);
		}
	}

	public void configureLibraries(final Bundle bundle) {
		final Handler handler = new Handler();
		AsyncJob.OBJECT$.doInBackground(new AsyncJob.OnBackgroundJob() {
			@Override
			public void doOnBackground() {
				Libs libs;
				String[] fields = null;
				String[] internalLibraries;
				String[] excludeLibraries;
				if (bundle != null) {
					fields = bundle.getStringArray(Libs.BUNDLE_FIELDS);
					internalLibraries = bundle.getStringArray(Libs.BUNDLE_LIBS);
					excludeLibraries = bundle.getStringArray(Libs.BUNDLE_EXCLUDE_LIBS);
					autoDetect = bundle.getBoolean(Libs.BUNDLE_AUTODETECT, true);
					sort = bundle.getBoolean(Libs.BUNDLE_SORT, true);
					animate = bundle.getBoolean(Libs.BUNDLE_ANIMATE, true);
					showLicense = bundle.getBoolean(Libs.BUNDLE_LICENSE, false);
					showLicenseDialog = bundle.getBoolean(Libs.BUNDLE_LICENSE_DIALOG, true);
					showVersion = bundle.getBoolean(Libs.BUNDLE_VERSION, false);
					try {
						libraryModification = (HashMap<String, HashMap<String, String>>) bundle.getSerializable(Libs.BUNDLE_LIBS_MODIFICATION);
					} catch (Exception ignored) {
					}
				} else {
					internalLibraries = null;
					excludeLibraries = null;
				}
				//init the Libs instance with fields if they were set
				if (fields == null) {
					libs = new Libs(getContext());
				} else {
					libs = new Libs(getContext(), fields);
				}
				//The last step is to look if we would love to show some about text for this project
				if (bundle != null && bundle.containsKey(Libs.BUNDLE_APP_ABOUT_ICON)) {
					aboutShowIcon = bundle.getBoolean(Libs.BUNDLE_APP_ABOUT_ICON);
				} else {
					String descriptionShowIcon = libs.getStringResourceByName("aboutLibraries_description_showIcon");
					if (!TextUtils.isEmpty(descriptionShowIcon)) {
						try {
							aboutShowIcon = Boolean.parseBoolean(descriptionShowIcon);
						} catch (Exception ignored) {
						}
					}
				}
				if (bundle != null && bundle.containsKey(Libs.BUNDLE_APP_ABOUT_VERSION)) {
					aboutShowVersion = bundle.getBoolean(Libs.BUNDLE_APP_ABOUT_VERSION);
				} else {
					String descriptionShowVersion = libs.getStringResourceByName("aboutLibraries_description_showVersion");
					if (!TextUtils.isEmpty(descriptionShowVersion)) {
						try {
							aboutShowVersion = Boolean.parseBoolean(descriptionShowVersion);
						} catch (Exception ignored) {
						}
					}
				}
				if (bundle != null && bundle.containsKey(Libs.BUNDLE_APP_ABOUT_DESCRIPTION)) {
					aboutDescription = bundle.getString(Libs.BUNDLE_APP_ABOUT_DESCRIPTION);
				} else {
					aboutDescription = libs.getStringResourceByName("aboutLibraries_description_text");
				}
				//apply modifications
				libs.modifyLibraries(libraryModification);
				//fetch the libraries and sort if a comparator was set
				libraries = libs.prepareLibraries(internalLibraries, excludeLibraries, autoDetect, sort);
				if (comparator != null) {
					Collections.sort(libraries, comparator);
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						generateAboutThisAppSection();
						showLibs();
					}
				});
			}
		});
	}

	private void showLibs() {
		mAdapter.setLibs(libraries);
		if (animate) {
			Animation fadeIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
			fadeIn.setDuration(500);
			LayoutAnimationController layoutAnimationController = new LayoutAnimationController(fadeIn);
			mRecyclerView.setLayoutAnimation(layoutAnimationController);
			mRecyclerView.startLayoutAnimation();
		}
	}

	private void generateAboutThisAppSection() {
		if (aboutShowIcon != null && aboutShowVersion != null) {
			//get the packageManager to load and read some values :D
			PackageManager pm = getContext().getPackageManager();
			//get the packageName
			String packageName = getContext().getPackageName();
			//Try to load the applicationInfo
			ApplicationInfo appInfo = null;
			PackageInfo packageInfo = null;
			try {
				appInfo = pm.getApplicationInfo(packageName, 0);
				packageInfo = pm.getPackageInfo(packageName, 0);
			} catch (Exception ignored) {
			}
			//Set the Icon or hide it
			Drawable icon = null;
			if (aboutShowIcon && appInfo != null) {
				icon = appInfo.loadIcon(pm);
			}
			//set the Version or hide it
			String versionName = null;
			Integer versionCode = null;
			if (packageInfo != null) {
				versionName = packageInfo.versionName;
				versionCode = packageInfo.versionCode;
			}
			//add this cool thing to the headerView of our listView
			mAdapter.setHeader(getContext().getString(getContext().getApplicationInfo().labelRes),aboutDescription, versionName, versionCode, aboutShowVersion, true, true, icon, aboutShowIcon);
		}
	}
}
