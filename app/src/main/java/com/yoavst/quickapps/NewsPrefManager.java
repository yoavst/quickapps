package com.yoavst.quickapps;

import android.content.Context;

import com.tale.prettysharedpreferences.LongEditor;
import com.tale.prettysharedpreferences.PrettySharedPreferences;
import com.tale.prettysharedpreferences.StringEditor;

public class NewsPrefManager extends PrettySharedPreferences<NewsPrefManager> {

    public NewsPrefManager(Context context) {
        super(context.getApplicationContext().getSharedPreferences("pref", Context.MODE_PRIVATE));
    }

    public StringEditor<NewsPrefManager> userId() {
        return getStringEditor("userId");
    }

    public StringEditor<NewsPrefManager> refreshToken() {
        return getStringEditor("refreshToken");
    }

	public StringEditor<NewsPrefManager> accessToken() {
		return getStringEditor("accessToken");
	}

	public StringEditor<NewsPrefManager> rawResponse() {
		return getStringEditor("rawResponse");
	}

	public StringEditor<NewsPrefManager> feed() {
		return getStringEditor("feed");
	}

	public LongEditor<NewsPrefManager> lastUpdateTime() {
		return getLongEditor("lastUpdateTime");
	}
}