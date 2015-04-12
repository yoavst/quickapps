package com.lge.qcircle.template;

/**
 * The {@code QCircleTemplateElement} class is an abstract class for LG Quick Circle UI components.
 * If you create a custom UI component for the QCircleTemplate, you will create a class extends {@code QCircleTemplateElement}.
 * @author sujin.cho
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
     * @return element view. This view is a root view of the QCircleTemplateElement.
     */
    public abstract View getView();

    /**
     * gets the ID of the element view.
     *
     * @return ID of the element view
     */
    public abstract int getId();

    /**
     * Adds this to the parent. The parent layout is a circle layout and the content layout is a content area.
     * The view of element is added to circle layout. Layout parameters of The content layout are adjusted.
     * @param parent
     * @param content
     * @see com.lge.qcircle.template.QCircleTemplate
     */
    protected abstract void addTo(RelativeLayout parent, RelativeLayout content);
}