package com.yoavst.quickapps;

import android.content.Context;

import com.tale.prettysharedpreferences.BooleanEditor;
import com.tale.prettysharedpreferences.IntegerEditor;
import com.tale.prettysharedpreferences.PrettySharedPreferences;
import com.tale.prettysharedpreferences.StringEditor;

public class PrefManager extends PrettySharedPreferences<PrefManager> {

	public PrefManager(Context context) {
		super(context.getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE));
	}

	public BooleanEditor<PrefManager> torchForceFloating() {
		return getBooleanEditor("torchForceFloating");
	}

	public BooleanEditor<PrefManager> showRepeatingEvents() {
		return getBooleanEditor("showRepeatingEvents");
	}

	public BooleanEditor<PrefManager> showDoubleTapDialog() {
		return getBooleanEditor("showDoubleTapDialog");
	}

	public BooleanEditor<PrefManager> startActivityOnNotification() {
		return getBooleanEditor("startActivityOnNotification");
	}

	public BooleanEditor<PrefManager> notificationShowContent() {
		return getBooleanEditor("notificationShowContent");
	}

	public BooleanEditor<PrefManager> launcherIsVertical() {
		return getBooleanEditor("launcherIsVertical");
	}

	public BooleanEditor<PrefManager> showAppsThatInLg() {
		return getBooleanEditor("showAppsThatInLg");
	}

	public BooleanEditor<PrefManager> launcherLoadExternalModules() {
		return getBooleanEditor("launcherLoadExternalModules");
	}

	public BooleanEditor<PrefManager> launcherAutoAddModules() {
		return getBooleanEditor("launcherAutoAddModules");
	}

	public BooleanEditor<PrefManager> showBatteryToggle() {
		return getBooleanEditor("showBatteryToggle");
	}

	public BooleanEditor<PrefManager> calculatorForceFloating() {
		return getBooleanEditor("calculatorForceFloating");
	}

	public BooleanEditor<PrefManager> stopwatchShowMillis() {
		return getBooleanEditor("stopwatchShowMillis");
	}

	public BooleanEditor<PrefManager> amPmInNotifications() {
		return getBooleanEditor("amPmInNotifications");
	}

	public BooleanEditor<PrefManager> dialerStartWithZero() {
		return getBooleanEditor("dialerStartWithZero");
	}

	public BooleanEditor<PrefManager> showLocation() {
		return getBooleanEditor("showLocation");
	}

	public BooleanEditor<PrefManager> amPmInCalendar() {
		return getBooleanEditor("amPmInCalendar");
	}

	public BooleanEditor<PrefManager> g2Mode() {
		return getBooleanEditor("g2Mode");
	}

	public IntegerEditor<PrefManager> highScoreInSimon() { return getIntegerEditor("highScoreInSimon"); }

	public StringEditor<PrefManager> launcherItems() {
		return getStringEditor("launcherItems");
	}

	public StringEditor<PrefManager> togglesItems() {
		return getStringEditor("togglesItems");
	}

	public StringEditor<PrefManager> quickDials() {
		return getStringEditor("quickDials");
	}

}