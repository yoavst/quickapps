package com.yoavst.quickapps.ball

import android.animation.Animator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.lge.qcircle.template.ButtonTheme
import com.lge.qcircle.template.QCircleBackButton
import com.lge.qcircle.template.TemplateTag
import com.yoavst.kotlin.colorRes
import com.yoavst.kotlin.stringArrayResource
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import com.yoavst.quickapps.tools.getBackgroundDrawable
import com.yoavst.quickapps.tools.viewanimations.ShakeAnimator
import com.yoavst.quickapps.tools.viewanimations.YoYo
import java.util.Random
import kotlin.properties.Delegates


public class CMagic8BallActivity : QCircleActivity() {
    override fun getIntentToShow(): Intent? = null
    val text: TextView by Delegates.lazy { generateTextView() }
    val answers by stringArrayResource(R.array.magic_ball)
    val random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        template.addElement(QCircleBackButton(this, ButtonTheme.DARK, true))
        template.setBackgroundColor(colorRes(R.color.md_indigo_500))
        text.setOnClickListener {
            YoYo.with(ShakeAnimator()).duration(1000).withListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    text.setText(R.string.thinking)
                }

                override fun onAnimationEnd(animation: Animator) {
                    val selected = random.nextInt(answers.size())
                    text.setText(answers[selected])
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            }).playOn( template.getLayoutById(TemplateTag.CONTENT_MAIN))
        }
        setContentViewToMain(text)
        setContentView(template.getView())
    }

    private fun generateTextView(): TextView {
        val textView = TextView(this)
        textView.setTextColor(Color.WHITE)
        textView.setTextSize(30F)
        textView.setGravity(Gravity.CENTER)
        textView.setText(R.string.ask_a_question)
        textView.setId(android.R.id.text1)
        textView.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_start), 0,
                getResources().getDimensionPixelSize(R.dimen.padding_end), 0)
        textView.setBackground(getBackgroundDrawable(colorRes(R.color.md_indigo_500), colorRes(R.color.ripple_material_dark)))
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        textView.setLayoutParams(params)
        return textView
    }
}