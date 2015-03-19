package com.lge.qcircle.template;

/**
 * Created by sujin.cho on 2015-03-11.
 */

import android.view.View;
import android.widget.RelativeLayout;
import android.content.Context;

public abstract class QCircleTemplateElement {

    protected Context mContext = null;

    public QCircleTemplateElement(){

    }

    /**
     * creates a template element.
     * @param context {@code Activity} which has a circle view.<br>
     * <b>If it is null, you might get errors when you use method of this class.</b>
     */
    public QCircleTemplateElement(Context context) {
        mContext = context;
    }

    /**
     * gets the view of the element
     *
     * @return element view
     */
    public abstract View getView();

    /**
     * gets the ID of the element view.
     *
     * @return ID of the element view
     */
    public abstract int getId();

    protected abstract void setElement(RelativeLayout parent);


}