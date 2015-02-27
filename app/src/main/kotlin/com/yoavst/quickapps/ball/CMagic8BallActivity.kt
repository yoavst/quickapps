package com.yoavst.quickapps.ball

import com.yoavst.quickapps.util.QCircleActivity
import com.lge.qcircle.template.QCircleTemplate
import kotlin.properties.Delegates
import com.lge.qcircle.template.TemplateType
import android.content.Intent
import android.widget.TextView
import java.util.Random
import android.widget.RelativeLayout
import android.graphics.Color
import android.view.Gravity
import com.yoavst.quickapps.R
import android.view.ViewGroup
import com.mobsandgeeks.ake.getStringArray
import com.lge.qcircle.template.TemplateTag
import android.os.Bundle
import com.daimajia.androidanimations.library.YoYo
import com.daimajia.androidanimations.library.Techniques
import com.nineoldandroids.animation.Animator

/**
 * Created by Yoav.
 */
public class CMagic8BallActivity: QCircleActivity() {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    val text: TextView by Delegates.lazy { generateTextView() }
    val answers: Array<String> by Delegates.lazy { getStringArray(R.array.magic_ball) }
    val layout: RelativeLayout by Delegates.lazy { template.getLayoutById(TemplateTag.CONTENT_MAIN) }
    val random = Random()
    override fun getIntentToShow(): Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        template.setBackButton()
        template.setBackButtonTheme(true)
        template.setBackgroundColor(getResources().getColor(R.color.md_indigo_500), true)
        layout.addView(text)
        setContentView(template.getView())
    }

    protected override fun onSingleTapConfirmed(): Boolean {
        YoYo.with(Techniques.Shake).duration(1000).withListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                text.setText(R.string.thinking)
            }

            override fun onAnimationEnd(animation: Animator) {
                val selected = random.nextInt(answers.size)
                text.setText(answers[selected])
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        }).playOn(layout)
        return true
    }

    private fun generateTextView(): TextView {
        val textView = TextView(this)
        textView.setTextColor(Color.WHITE)
        textView.setTextSize(35F)
        textView.setGravity(Gravity.CENTER)
        textView.setText(R.string.ask_a_question)
        textView.setId(android.R.id.text1)
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        params.setMarginStart(getResources().getDimensionPixelSize(R.dimen.padding_start))
        params.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.padding_end))
        textView.setLayoutParams(params)
        return textView
    }
}
