package com.lge.qcircle.template;

/**
 * The {@code TemplateType} enumeration defines constants for design templates which are supported by {@link QCircleTemplate}.  <P>
 * Use the constant when you set the circle template using {@link QCircleTemplate#QCircleTemplate(android.content.Context, com.lge.qcircle.template.TemplateType)}.
 * @author jeongeun.jeon
 * @see QCircleTemplate
 *
 */
public enum TemplateType {
	/**
	 * empty template. <P> It just has empty main content and can has a title or a back button if you want.
	 */
	CIRCLE_EMPTY,
	/**
	 * template including a content and a vertical sidebar. <P> It can has a title or a back button if you want.
	 */
	CIRCLE_VERTICAL,
	/**
	 * template including a content and a horizontal sidebar. <P> It can has a title or a back button if you want.
	 */
	CIRCLE_HORIZONTAL,
	/**
	 * template including a content and two topbars.<P> It can has a title or a back button if you want.
	 */
	CIRCLE_COMPLEX,
	/**
	 * tmeplate including a content and two sidebars. The sidebars are on the left side and right side of the content. <P> It can has a title or a back button if you want.
	 */
	CIRCLE_SIDEBAR
}

