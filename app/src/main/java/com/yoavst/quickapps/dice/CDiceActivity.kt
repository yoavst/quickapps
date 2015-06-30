package com.yoavst.quickapps.dice

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.yoavst.kotlin.*
import com.yoavst.quickapps.R
import com.yoavst.quickapps.tools.QCircleActivity
import com.yoavst.quickapps.tools.getBackgroundDrawable
import com.yoavst.quickapps.tools.viewanimations.ShakeAnimator
import com.yoavst.quickapps.tools.viewanimations.YoYo
import kotlinx.android.synthetic.dice_activity.*
import java.util.Random
import kotlin.properties.Delegates

public class CDiceActivity : QCircleActivity() {
    var lock = false
    val drawables by Delegates.lazy {
        arrayOf(drawable1, drawable2, drawable3, drawable4, drawable5, drawable6)
    }
    val drawable1 by drawableResource(R.drawable.d1)
    val drawable2 by drawableResource(R.drawable.d2)
    val drawable3 by drawableResource(R.drawable.d3)
    val drawable4 by drawableResource(R.drawable.d4)
    val drawable5 by drawableResource(R.drawable.d5)
    val drawable6 by drawableResource(R.drawable.d6)

    val dices by Delegates.lazy { arrayOf(dice0, dice1, dice2, dice3, dice4) }
    var count = 9
    val handler = Handler()
    val random = Random()
    val loop: Runnable = Runnable {
        dices.forEach {
            if (it.getVisibility() == View.VISIBLE)
                it.setImageDrawable(drawables[random.nextInt(6)])
        }
        count--
        if (count > 0) handler.postDelayed(loop, 100)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewToMain(R.layout.dice_activity)
        setContentView(template.getView())
        back.setOnClickListener {
            finish()
        }
        add.setOnClickListener {
            if (dice0.getVisibility() == View.VISIBLE) {
                dice0.hide()
                dice1.show()
                dice2.show()
            } else if (dice4.getVisibility() != View.VISIBLE) {
                if (dice3.getVisibility() == View.VISIBLE)
                    dice4.show()
                else dice3.show()
            }
        }
        remove.setOnClickListener {
            if (dice4.getVisibility() == View.VISIBLE)
                dice4.hide()
            else if (dice3.getVisibility() == View.VISIBLE)
                dice3.hide()
            else if (dice2.getVisibility() == View.VISIBLE) {
                dice0.show()
                dice1.hide()
                dice2.hide()
            }
        }

        diceLayout.setOnClickListener {
            if (!lock) {
                lock = true
                YoYo.with(ShakeAnimator.getBigShake()).duration(1000).withListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        count = 9
                        handler.post(loop)
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        lock = false
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }
                }).playOn(diceLayout)
            }
        }
    }

    override fun getIntentToShow(): Intent? {
        return null
    }

}