package com.lge.qcircle.template;

/**
 * The {@code TemplateTag} class provides IDs of content views which are on the circle.<P>
 * You can get the layout of a content view by using {@link QCircleTemplate#getLayoutById(int)} with a constant of this class.<BR>
 *  
 * @see QCircleTemplate#getLayoutById(int)
 * @author jeongeun.jeon
 *
 */
public final class TemplateTag {
	/**
	 * top content. <P>
	 * If it has any children, you can set them, too.
	 */
	public static final int CONTENT = R.id.content;
	
	/**
	 * main content.<P>
	 * If you use an empty template, it is identical to {@link #CONTENT}.
	 * If you use an template which has any side bars, it means a child view of the top content excluding sidebars.
	 */
	public static final int CONTENT_MAIN = R.id.main;
	
	/**
	 * the first sidebar.<P>
	 * If you use an empty template, it cannot be used.
	 * If you use an template which has any side bars, it means the first sidebar.
	 */
	public static final int CONTENT_SIDE_1 = R.id.side1;
	
	/**
	 * the second sidebar.<P>
	 * If you use an empty template or an template with one sidebar, it cannot be used.
	 * If you use an template which has two side bars, it means the second sidebar.
	 */
	public static final int CONTENT_SIDE_2 = R.id.side2;


}
