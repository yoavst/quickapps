package com.yoavst.quickapps.music;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

import com.yoavst.quickapps.R;

/**
 * A simple fab that switches between play and pause states
 */
public class PlayPauseView extends FloatingActionButton {
    public PlayPauseView(Context context) {
        super(context);
        setPausing();
    }

    public PlayPauseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPausing();
    }

    public PlayPauseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPausing();
    }

    /**
     * Sets pause mode look for fab.
     */
    public void setPausing() {
        setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));
        setImageResource(R.drawable.ic_play);
    }

    /**
     * Sets play mode look for fab.
     */
    public void setPlaying() {
        setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent)));
        setImageResource(R.drawable.ic_pause);
    }
}
