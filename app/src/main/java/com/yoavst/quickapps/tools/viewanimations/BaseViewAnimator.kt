/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 daimajia
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yoavst.quickapps.tools.viewanimations

import android.animation.Animator
import android.animation.AnimatorSet
import android.view.View
import android.view.animation.Interpolator

public abstract class BaseViewAnimator {

    public var animatorAgent: AnimatorSet = AnimatorSet()
        private set
    public var duration: Long = DURATION
        private set
    
    protected abstract fun prepare(target: View)

    public fun setTarget(target: View): BaseViewAnimator {
        reset(target)
        prepare(target)
        return this
    }

    public fun animate() {
        start()
    }

    /**
     * reset the view to default status

     * @param target
     */
    public fun reset(target: View) {
        target.setAlpha(1f)
        target.setScaleX(1f)
        target.setScaleY(1f)
        target.setTranslationX(0f)
        target.setTranslationY(0f)
        target.setRotation(0f)
        target.setRotationY(0f)
        target.setRotationX(0f)
        target.setPivotX(target.getMeasuredWidth() / 2.0f)
        target.setPivotY(target.getMeasuredHeight() / 2.0f)
    }

    /**
     * start to animate
     */
    public fun start() {
        animatorAgent.setDuration(duration)
        animatorAgent.start()
    }

    public fun setDuration(duration: Long): BaseViewAnimator {
        this.duration = duration
        return this
    }

    public fun setStartDelay(delay: Long): BaseViewAnimator {
        animatorAgent.setStartDelay(delay)
        return this
    }

    public fun getStartDelay(): Long {
        return animatorAgent.getStartDelay()
    }

    public fun addAnimatorListener(l: Animator.AnimatorListener): BaseViewAnimator {
        animatorAgent.addListener(l)
        return this
    }

    public fun cancel() {
        animatorAgent.cancel()
    }

    public fun isRunning(): Boolean {
        return animatorAgent.isRunning()
    }

    public fun isStarted(): Boolean {
        return animatorAgent.isStarted()
    }

    public fun removeAnimatorListener(l: Animator.AnimatorListener) {
        animatorAgent.removeListener(l)
    }

    public fun removeAllListener() {
        animatorAgent.removeAllListeners()
    }

    public fun setInterpolator(interpolator: Interpolator?): BaseViewAnimator {
        animatorAgent.setInterpolator(interpolator)
        return this
    }

    companion object {

        public val DURATION: Long = 1000
    }

}