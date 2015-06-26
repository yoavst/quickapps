package com.yoavst.quickapps.tools;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yoavst.quickapps.R;

public class ForegroundImageView extends ImageView {
    private Drawable foreground;

    public ForegroundImageView(Context context) {
        this(context, null);
    }

    public ForegroundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundImageView);
        Drawable foreground = a.getDrawable(R.styleable.ForegroundImageView_android_foreground);
        if (foreground != null) {
            setForeground(foreground);
        }
        a.recycle();
    }

    /**
     * Supply a drawable resource that is to be rendered on top of all of the child
     * views in the frame layout.
     *
     * @param drawableResId The drawable resource to be drawn on top of the children.
     */
    public void setForegroundResource(int drawableResId) {
        setForeground(getContext().getResources().getDrawable(drawableResId));
    }

    /**
     * Supply a Drawable that is to be rendered on top of all of the child
     * views in the frame layout.
     *
     * @param drawable The Drawable to be drawn on top of the children.
     */
    public void setForeground(Drawable drawable) {
        if (foreground == drawable) {
            return;
        }
        if (foreground != null) {
            foreground.setCallback(null);
            unscheduleDrawable(foreground);
        }

        foreground = drawable;

        if (drawable != null) {
            drawable.setCallback(this);
            if (drawable.isStateful()) {
                drawable.setState(getDrawableState());
            }
        }
        drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        invalidate();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == foreground || super.verifyDrawable(who);
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (foreground != null) foreground.jumpToCurrentState();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (foreground != null && foreground.isStateful()) {
            foreground.setState(getDrawableState());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (foreground != null) {
            foreground.setBounds(0, 0, w, h);
            invalidate();
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);

        if (foreground != null) {
            foreground.draw(canvas);
        }
    }
}
